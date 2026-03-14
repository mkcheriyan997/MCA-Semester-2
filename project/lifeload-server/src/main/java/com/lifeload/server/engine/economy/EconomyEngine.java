package com.lifeload.server.engine.economy;

import com.lifeload.server.entity.Investment;
import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.PlayerStats;
import com.lifeload.server.repository.InvestmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class EconomyEngine {

    @Autowired
    private InvestmentRepository investmentRepository;

    private String currentMarketState = "GROWTH"; // BOOM, GROWTH, RECESSION, CRASH
    private final Random random = new Random();

    public void updateMarketState() {
        double r = random.nextDouble();
        if (currentMarketState.equals("BOOM")) {
            if (r < 0.2) currentMarketState = "CRASH";
            else if (r < 0.6) currentMarketState = "GROWTH";
        } else if (currentMarketState.equals("GROWTH")) {
            if (r < 0.1) currentMarketState = "BOOM";
            else if (r < 0.3) currentMarketState = "RECESSION";
        } else if (currentMarketState.equals("RECESSION")) {
            if (r < 0.1) currentMarketState = "CRASH";
            else if (r < 0.4) currentMarketState = "GROWTH";
        } else if (currentMarketState.equals("CRASH")) {
            if (r < 0.5) currentMarketState = "RECESSION";
        }
    }

    public void processInvestments(PlayerProfile profile) {
        List<Investment> investments = investmentRepository.findByPlayerProfile(profile);
        for (Investment inv : investments) {
            double changeFactor = 1.0;
            switch (currentMarketState) {
                case "BOOM": changeFactor = 1.05 + random.nextDouble() * 0.10; break;
                case "GROWTH": changeFactor = 1.01 + random.nextDouble() * 0.05; break;
                case "RECESSION": changeFactor = 0.95 - random.nextDouble() * 0.05; break;
                case "CRASH": changeFactor = 0.80 - random.nextDouble() * 0.15; break;
            }
            
            // Startups are higher volatility
            if ("STARTUP".equals(inv.getType())) {
                if (changeFactor > 1.0) changeFactor += 0.10;
                else changeFactor -= 0.15;
            }
            
            inv.setCurrentAmount(inv.getCurrentAmount() * changeFactor);
            investmentRepository.save(inv);
        }
    }
    
    public String getCurrentMarketState() {
        return currentMarketState;
    }
}
