package com.lifeload.server.controller;

import com.lifeload.server.engine.economy.EconomyEngine;
import com.lifeload.server.entity.Investment;
import com.lifeload.server.entity.PlayerProfile;
import com.lifeload.server.entity.User;
import com.lifeload.server.repository.InvestmentRepository;
import com.lifeload.server.repository.PlayerProfileRepository;
import com.lifeload.server.repository.UserRepository;
import com.lifeload.server.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/economy")
public class EconomyController {

    @Autowired private EconomyEngine economyEngine;
    @Autowired private InvestmentRepository investmentRepository;
    @Autowired private PlayerProfileRepository profileRepository;
    @Autowired private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId()).orElseThrow();
    }

    @GetMapping("/market")
    public ResponseEntity<?> getMarketState() {
        Map<String, String> response = new HashMap<>();
        response.put("state", economyEngine.getCurrentMarketState());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invest")
    @Transactional
    public ResponseEntity<?> invest(@RequestBody Map<String, Object> request) {
        try {
            User user = getCurrentUser();
            Optional<PlayerProfile> profileOpt = profileRepository.findByUser(user);
            if (profileOpt.isEmpty()) return ResponseEntity.badRequest().body("No game.");
            PlayerProfile profile = profileOpt.get();

            double amount = Double.parseDouble(request.get("amount").toString());
            String type = (String) request.get("type");
            String name = (String) request.get("name");

            if (profile.getStats().getMoney() < amount) {
                profile.getStats().setStress(profile.getStats().getStress() + 15);
                profileRepository.save(profile);
                return ResponseEntity.badRequest().body("Not enough money! The market anxiety spiked your stress.");
            }

            profile.getStats().setMoney(profile.getStats().getMoney() - amount);
            profileRepository.save(profile);

            Investment inv = new Investment();
            inv.setPlayerProfile(profile);
            inv.setType(type);
            inv.setName(name);
            inv.setInitialAmount(amount);
            inv.setCurrentAmount(amount);
            inv.setInvestedAtAge(profile.getAge());
            inv.setInvestedAtWeek(profile.getCurrentWeek());
            investmentRepository.save(inv);

            return ResponseEntity.ok("Investment successful");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/portfolio")
    public ResponseEntity<?> getPortfolio() {
        try {
            User user = getCurrentUser();
            Optional<PlayerProfile> profileOpt = profileRepository.findByUser(user);
            if (profileOpt.isEmpty()) return ResponseEntity.badRequest().body("No game.");
            
            return ResponseEntity.ok(investmentRepository.findByPlayerProfile(profileOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/sell/{investmentId}")
    @Transactional
    public ResponseEntity<?> sellInvestment(@PathVariable("investmentId") Long investmentId) {
        try {
            User user = getCurrentUser();
            Optional<PlayerProfile> profileOpt = profileRepository.findByUser(user);
            if (profileOpt.isEmpty()) return ResponseEntity.badRequest().body("No game.");
            PlayerProfile profile = profileOpt.get();

            Optional<Investment> invOpt = investmentRepository.findById(investmentId);
            if (invOpt.isEmpty()) return ResponseEntity.badRequest().body("Investment not found.");
            
            Investment inv = invOpt.get();
            if (!inv.getPlayerProfile().getId().equals(profile.getId())) {
                return ResponseEntity.badRequest().body("You do not own this investment.");
            }

            profile.getStats().setMoney(profile.getStats().getMoney() + inv.getCurrentAmount());
            profileRepository.save(profile);
            
            investmentRepository.delete(inv);

            return ResponseEntity.ok("Sold investment successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
