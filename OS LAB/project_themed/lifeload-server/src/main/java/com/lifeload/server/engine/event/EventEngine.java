package com.lifeload.server.engine.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeload.server.entity.GameEvent;
import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.PlayerStats;
import com.lifeload.server.repository.GameEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class EventEngine {

    @Autowired
    private GameEventRepository gameEventRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private final Random random = new Random();

    public GameEvent triggerRandomEvent(PlayerProfile profile) {
        // Only trigger an event 20% of the time, or if stress is very high etc.
        PlayerStats stats = profile.getStats();
        
        double chance = 0.20;
        if (stats.getStress() > 80) chance += 0.30;
        if (stats.getHappiness() < 20) chance += 0.20;
        
        if (random.nextDouble() > chance) {
            return null; // No event this week
        }

        List<GameEvent> validEvents = gameEventRepository.findValidEvents(
            profile.getAge(), stats.getStress(), stats.getMoney()
        );
        
        if (validEvents.isEmpty()) return null;
        
        // Pick one randomly
        return validEvents.get(random.nextInt(validEvents.size()));
    }
}
