package com.lifeload.server.engine;

import com.lifeload.server.entity.NpcRival;
import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.PlayerStats;
import com.lifeload.server.entity.Trait;
import com.lifeload.server.repository.NpcRivalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class RivalService {

    @Autowired
    private NpcRivalRepository npcRivalRepository;

    private static final String[][] RIVAL_TEMPLATES = {
        {"Alex Chen", "COWORKER", "Climbing the corporate ladder"},
        {"Maya Patel", "STARTUP_FOUNDER", "Building the next unicorn"},
        {"James Whitfield", "INVESTOR", "Mastering the stock market"},
        {"Sofia Torres", "COWORKER", "Competing for the same promotion"},
        {"Ryan Lee", "STARTUP_FOUNDER", "Disrupting your industry"}
    };

    private final Random random = new Random();

    public void initRivals(PlayerProfile profile) {
        // Assign 2-3 rival NPCs when the game starts
        deleteExistingRivals(profile);
        int count = 2 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            String[] template = RIVAL_TEMPLATES[i % RIVAL_TEMPLATES.length];
            NpcRival rival = new NpcRival();
            rival.setPlayerProfile(profile);
            rival.setName(template[0]);
            rival.setType(template[1]);
            rival.setCurrentGoal(template[2]);
            rival.setWealth(300 + random.nextDouble() * 500);
            rival.setLevel(1);
            rival.setAgeStarted(profile.getAge());
            npcRivalRepository.save(rival);
        }
    }

    public void progressRivals(PlayerProfile profile) {
        List<NpcRival> rivals = npcRivalRepository.findByPlayerProfile(profile);
        for (NpcRival rival : rivals) {
            // Rivals grow each week based on type
            double growth = switch (rival.getType()) {
                case "INVESTOR" -> rival.getWealth() * (0.005 + random.nextDouble() * 0.01);
                case "STARTUP_FOUNDER" -> rival.getWealth() * (0.002 + random.nextDouble() * 0.02);
                default -> 200 + random.nextDouble() * 100; // COWORKER earns salary
            };
            rival.setWealth(rival.getWealth() + growth);
            if (rival.getWealth() > rival.getLevel() * 10000) {
                rival.setLevel(rival.getLevel() + 1);
            }
            npcRivalRepository.save(rival);
        }
    }

    public List<NpcRival> getRivals(PlayerProfile profile) {
        return npcRivalRepository.findByPlayerProfile(profile);
    }

    private void deleteExistingRivals(PlayerProfile profile) {
        List<NpcRival> existing = npcRivalRepository.findByPlayerProfile(profile);
        npcRivalRepository.deleteAll(existing);
    }
}
