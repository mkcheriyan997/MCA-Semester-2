package com.lifeload.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.lifeload.server.engine.AchievementManager;
import com.lifeload.server.engine.RivalService;
import com.lifeload.server.engine.action.ActionProcessor;
import com.lifeload.server.engine.event.EventEngine;
import com.lifeload.server.entity.*;
import com.lifeload.server.payload.request.ActionRequest;
import com.lifeload.server.payload.request.EventChoiceRequest;
import com.lifeload.server.repository.*;
import com.lifeload.server.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired private PlayerProfileRepository profileRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ActionProcessor actionProcessor;
    @Autowired private LeaderboardEntryRepository leaderboardRepo;
    @Autowired private AchievementManager achievementManager;
    @Autowired private RivalService rivalService;
    @Autowired private TimelineEventRepository timelineEventRepository;
    @Autowired private DailyRewardRepository dailyRewardRepository;
    @Autowired private EventEngine eventEngine;
    @Autowired private com.lifeload.server.engine.economy.EconomyEngine economyEngine;
    @Autowired private ObjectMapper objectMapper;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId()).orElseThrow();
    }

    // ── START GAME ─────────────────────────────────────────────────────────────
    @PostMapping("/start")
    @Transactional
    public ResponseEntity<?> startGame(@RequestBody Map<String, Object> request) {
        try {
            User user = getCurrentUser();
            Optional<PlayerProfile> existingProfile = profileRepository.findByUser(user);
            if (existingProfile.isPresent()) {
                profileRepository.delete(existingProfile.get());
                profileRepository.flush(); // Force immediate deletion of orphans
            }

            PlayerProfile profile = new PlayerProfile();
            profile.setUser(user);
            profile.setPlayerName((String) request.getOrDefault("playerName", "Player"));

            PlayerStats stats = new PlayerStats();
            profile.setStats(stats);
            stats.setPlayerProfile(profile);

            profile = profileRepository.save(profile);

            // Init rivals
            rivalService.initRivals(profile);

            // Record starting timeline event
            recordTimelineEvent(profile, "Your life journey begins. The world is full of possibilities.",
                "START", profile.getAge(), profile.getCurrentWeek());

            return ResponseEntity.ok(buildGameResponse(profile));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // ── LOAD GAME ──────────────────────────────────────────────────────────────
    @GetMapping("/load")
    public ResponseEntity<?> loadGame() {
        User user = getCurrentUser();
        Optional<PlayerProfile> profile = profileRepository.findByUser(user);
        if (profile.isPresent()) {
            return ResponseEntity.ok(buildGameResponse(profile.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // ── PERFORM ACTION ─────────────────────────────────────────────────────────
    @PostMapping("/action")
    @Transactional
    public ResponseEntity<?> performAction(@RequestBody ActionRequest request) {
        System.out.println("DEBUG: Processing action: " + request.getActionId());
        try {
            User user = getCurrentUser();
            Optional<PlayerProfile> profileOpt = profileRepository.findByUser(user);
            if (profileOpt.isEmpty()) return ResponseEntity.badRequest().body("No game.");

            PlayerProfile profile = profileOpt.get();
            if (!"ACTIVE".equals(profile.getStatus())) return ResponseEntity.badRequest().body("Game over.");
            if (profile.getPendingEventJson() != null) return ResponseEntity.badRequest().body("Decision required.");

            String actionResult = actionProcessor.processAction(request.getActionId(), profile);
            if (!"SUCCESS".equals(actionResult)) {
                if ("INSUFFICIENT_FUNDS".equals(actionResult)) {
                    // Pre-mature save to persist the stress penalty even though the action failed
                    profileRepository.save(profile);
                    return ResponseEntity.badRequest().body("Not enough money! The financial anxiety caused a severe spike in your stress levels.");
                } else if ("INSUFFICIENT_ENERGY".equals(actionResult)) {
                    return ResponseEntity.badRequest().body("Not enough energy. Try resting.");
                } else if ("INSUFFICIENT_KNOWLEDGE".equals(actionResult)) {
                    return ResponseEntity.badRequest().body("You lack the required knowledge for this.");
                }
                return ResponseEntity.badRequest().body("Action failed: " + actionResult);
            }

            // Record timeline for major milestones
            checkAndRecordMilestones(profile);

            // Advance time
            advanceTime(profile);

            // Run rivals
            rivalService.progressRivals(profile);

            // Check achievements
            achievementManager.checkAchievements(profile);

            // Check secret endings / game over
            checkGameEnd(profile);

            // Trigger random events if not game over
            if ("ACTIVE".equals(profile.getStatus()) && profile.getPendingEventJson() == null) {
                GameEvent event = eventEngine.triggerRandomEvent(profile);
                if (event != null) {
                    profile.setPendingEventJson(objectMapper.writeValueAsString(event));
                }
            }

            profileRepository.save(profile);
            return ResponseEntity.ok(buildGameResponse(profile));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/event/choice")
    @Transactional
    public ResponseEntity<?> processEventChoice(@RequestBody EventChoiceRequest request) {
        try {
            User user = getCurrentUser();
            PlayerProfile profile = profileRepository.findByUser(user).orElseThrow();

            if (profile.getPendingEventJson() == null) {
                return ResponseEntity.badRequest().body("No pending event.");
            }

            JsonNode eventJson = objectMapper.readTree(profile.getPendingEventJson());
            JsonNode options = eventJson.get("options");
            int idx = request.getOptionIndex();

            if (idx < 0 || idx >= options.size()) {
                return ResponseEntity.badRequest().body("Invalid option index.");
            }

            JsonNode selectedOption = options.get(idx);
            double risk = selectedOption.get("riskProbability").asDouble();
            boolean failed = new Random().nextDouble() < risk;

            StatImpact impact;
            String outcomeDesc;
            if (failed) {
                impact = objectMapper.treeToValue(selectedOption.get("failImpact"), StatImpact.class);
                outcomeDesc = "FAILURE: " + eventJson.get("title").asText() + " - " + selectedOption.get("label").asText();
            } else {
                impact = objectMapper.treeToValue(selectedOption.get("impact"), StatImpact.class);
                outcomeDesc = "SUCCESS: " + eventJson.get("title").asText() + " - " + selectedOption.get("label").asText();
            }

            // Apply Impact
            PlayerStats stats = profile.getStats();
            applyStatImpact(stats, impact);

            // Record Timeline
            recordTimelineEvent(profile, outcomeDesc, "DECISION", profile.getAge(), profile.getCurrentWeek());

            // Clear pending event
            profile.setPendingEventJson(null);
            profileRepository.save(profile);

            return ResponseEntity.ok(buildGameResponse(profile));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    private void applyStatImpact(PlayerStats stats, StatImpact impact) {
        if (impact == null) return;
        stats.setHealth(stats.getHealth() + impact.getHealthDelta());
        stats.setHappiness(stats.getHappiness() + impact.getHappinessDelta());
        stats.setMoney(stats.getMoney() + impact.getMoneyDelta());
        stats.setKnowledge(stats.getKnowledge() + impact.getKnowledgeDelta());
        stats.setEnergy(stats.getEnergy() + impact.getEnergyDelta());
        stats.setRelationships(stats.getRelationships() + impact.getRelationshipsDelta());
        stats.setReputation(stats.getReputation() + impact.getReputationDelta());
        stats.setStress(stats.getStress() + impact.getStressDelta());
        stats.setConfidence(stats.getConfidence() + impact.getConfidenceDelta());
        stats.setMotivation(stats.getMotivation() + impact.getMotivationDelta());

        // Clamp stats
        stats.setHealth(Math.max(0, Math.min(100, stats.getHealth())));
        stats.setEnergy(Math.max(0, Math.min(100, stats.getEnergy())));
        stats.setHappiness(Math.max(0, Math.min(100, stats.getHappiness())));
        stats.setStress(Math.max(0, Math.min(100, stats.getStress())));
        stats.setRelationships(Math.max(0, Math.min(100, stats.getRelationships())));
        stats.setReputation(Math.max(0, Math.min(100, stats.getReputation())));
        stats.setConfidence(Math.max(0, Math.min(100, stats.getConfidence())));
        stats.setMotivation(Math.max(0, Math.min(100, stats.getMotivation())));
    }

    // ── TIMELINE ───────────────────────────────────────────────────────────────
    @GetMapping("/timeline")
    public ResponseEntity<?> getTimeline() {
        User user = getCurrentUser();
        Optional<PlayerProfile> profileOpt = profileRepository.findByUser(user);
        if (profileOpt.isEmpty()) return ResponseEntity.notFound().build();

        List<TimelineEvent> events = timelineEventRepository
            .findByPlayerProfileOrderByAgeAscWeekAsc(profileOpt.get());

        List<Map<String, Object>> result = new ArrayList<>();
        for (TimelineEvent e : events) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", e.getId());
            item.put("age", e.getAge());
            item.put("week", e.getWeek());
            item.put("description", e.getDescription());
            item.put("type", e.getEventType());
            item.put("claimable", e.isClaimable());
            item.put("claimed", e.isClaimed());
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/claim-milestone/{id}")
    @Transactional
    public ResponseEntity<?> claimMilestone(@PathVariable("id") Long id) {
        try {
            User user = getCurrentUser();
            TimelineEvent event = timelineEventRepository.findById(id).orElseThrow();
            
            if (!event.getPlayerProfile().getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("Not yours.");
            }

            if (!event.isClaimable() || event.isClaimed()) {
                return ResponseEntity.badRequest().body("Not claimable or already claimed.");
            }

            PlayerStats stats = event.getPlayerProfile().getStats();
            String bonusMsg = "";
            switch (event.getEventType()) {
                case "MONEY" -> { stats.setMoney(stats.getMoney() + 5000); bonusMsg = "Claimed $5,000 Milestone Bonus!"; }
                case "CAREER" -> { stats.setMotivation(Math.min(100, stats.getMotivation() + 30)); bonusMsg = "Claimed Motivation Boost!"; }
                case "RELATIONSHIP" -> { stats.setHappiness(Math.min(100, stats.getHappiness() + 25)); bonusMsg = "Claimed Happiness Boost!"; }
                default -> { stats.setMoney(stats.getMoney() + 1000); bonusMsg = "Claimed $1,000 Bonus!"; }
            }

            event.setClaimed(true);
            timelineEventRepository.save(event);
            return ResponseEntity.ok(Map.of("message", bonusMsg));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // ── RIVALS ─────────────────────────────────────────────────────────────────
    @GetMapping("/rivals")
    public ResponseEntity<?> getRivals() {
        User user = getCurrentUser();
        Optional<PlayerProfile> profileOpt = profileRepository.findByUser(user);
        if (profileOpt.isEmpty()) return ResponseEntity.notFound().build();

        List<NpcRival> rivals = rivalService.getRivals(profileOpt.get());
        List<Map<String, Object>> result = new ArrayList<>();
        for (NpcRival r : rivals) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("name", r.getName());
            item.put("type", r.getType());
            item.put("level", r.getLevel());
            item.put("wealth", r.getWealth());
            item.put("goal", r.getCurrentGoal());
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    // ── DAILY REWARD ───────────────────────────────────────────────────────────
    @GetMapping("/daily-reward")
    @Transactional
    public ResponseEntity<?> claimDailyReward() {
        User user = getCurrentUser();
        DailyReward reward = dailyRewardRepository.findByUser(user).orElseGet(() -> {
            DailyReward r = new DailyReward();
            r.setUser(user);
            return r;
        });

        LocalDate today = LocalDate.now();
        Map<String, Object> response = new HashMap<>();

        if (reward.getLastClaimedAt() != null && reward.getLastClaimedAt().equals(today)) {
            response.put("claimed", false);
            response.put("message", "Already claimed today. Come back tomorrow!");
            response.put("streak", reward.getStreak());
            return ResponseEntity.ok(response);
        }

        // Update streak
        if (reward.getLastClaimedAt() != null && reward.getLastClaimedAt().equals(today.minusDays(1))) {
            reward.setStreak(reward.getStreak() + 1);
        } else {
            reward.setStreak(1); // Streak reset
        }
        reward.setLastClaimedAt(today);
        dailyRewardRepository.save(reward);

        // Apply bonus to player if they have an active game
        Optional<PlayerProfile> profileOpt = profileRepository.findByUser(user);
        String bonusDesc = applyDailyBonus(profileOpt.orElse(null), reward.getStreak());

        response.put("claimed", true);
        response.put("streak", reward.getStreak());
        response.put("bonus", bonusDesc);
        return ResponseEntity.ok(response);
    }

    // ── MINIGAMES ──────────────────────────────────────────────────────────────
    @PostMapping("/minigame")
    @Transactional
    public ResponseEntity<?> playMiniGame(@RequestBody Map<String, Object> request) {
        try {
            User user = getCurrentUser();
            Optional<PlayerProfile> profileOpt = profileRepository.findByUser(user);
            if (profileOpt.isEmpty()) return ResponseEntity.badRequest().body("No game.");

            PlayerProfile profile = profileOpt.get();
            if (!"ACTIVE".equals(profile.getStatus())) return ResponseEntity.badRequest().body("Game over.");

            String type = (String) request.get("type");
            PlayerStats stats = profile.getStats();
            Map<String, Object> response = new HashMap<>();

            if ("BUDGET_QUIZ".equals(type)) {
                int score = (Integer) request.getOrDefault("score", 0);
                int mistakes = (Integer) request.getOrDefault("mistakes", 0);
                
                double reward = score * 100.0;
                double penalty = mistakes * 50.0;
                
                stats.setMoney(stats.getMoney() + reward - penalty);
                
                String msg = "Quiz Complete! Score: " + score + ". Earned $" + reward;
                if (score >= 8) {
                    stats.setKnowledge(stats.getKnowledge() + 10);
                    msg += " and +10 Knowledge.";
                } else if (score >= 5) {
                    stats.setKnowledge(stats.getKnowledge() + 5);
                    msg += " and +5 Knowledge.";
                }
                
                if (mistakes > 0) {
                    msg += "\n⚠️ " + mistakes + " Wrong Answers cost you $" + penalty + "!";
                }
                response.put("message", msg);
            } else if ("PRODUCTIVITY_FLOW".equals(type)) {
                int score = (Integer) request.getOrDefault("score", 0);
                int mistakes = (Integer) request.getOrDefault("mistakes", 0);
                
                // Rewards: $10 per point, +1 Motivation per 10 points
                double credits = score * 10.0;
                int motivationBonus = score / 10;
                
                // Penalties: -$20 per mistake, +2 Stress per mistake
                double penalty = mistakes * 20.0;
                int stressPenalty = mistakes * 2;
                
                stats.setMoney(stats.getMoney() + credits - penalty);
                stats.setMotivation(Math.min(100, stats.getMotivation() + motivationBonus));
                stats.setStress(Math.min(100, stats.getStress() + stressPenalty));
                stats.setEnergy(Math.max(0, stats.getEnergy() - 10)); // Game takes energy
                
                String msg = "Deep Work Complete! Score: " + score + ". Earned $" + credits + " and +" + motivationBonus + " Motivation.";
                if (mistakes > 0) {
                    msg += "\n⚠️ " + mistakes + " Distractions cost you $" + penalty + " and +" + stressPenalty + " Stress!";
                }
                response.put("message", msg);
            } else if ("MEMORY_MATRIX".equals(type)) {
                int score = (Integer) request.getOrDefault("score", 0);
                
                // Reward: $50 per level, +1 Knowledge per level
                double credits = score * 50.0;
                int knowledgeBonus = score;
                
                // Penalty: Failure costs $20 and a chunk of energy
                double penalty = 20.0;
                
                stats.setMoney(stats.getMoney() + credits - penalty);
                stats.setKnowledge(stats.getKnowledge() + knowledgeBonus);
                stats.setEnergy(Math.max(0, stats.getEnergy() - 25)); // Takes 25 energy (15 normal + 10 penalty for brain fry)
                
                String msg = "Memory Matrix Complete! Reached Level " + score + ". Earned $" + credits + " and +" + knowledgeBonus + " Knowledge.\n";
                msg += "⚠️ Mind overload cost you $" + penalty + " and extra energy.";
                response.put("message", msg);
            } else if ("TYPING_HUSTLE".equals(type)) {
                int score = (Integer) request.getOrDefault("score", 0);
                int mistakes = (Integer) request.getOrDefault("mistakes", 0);
                
                // New logic: $4 per point, +1 Reputation per 25 points
                double credits = score * 4.0;
                int repBonus = score / 25;
                
                // Penalty: -$20 per typo, -1 Reputation per 2 typos
                double penalty = mistakes * 20.0;
                int repPenalty = mistakes / 2;
                
                stats.setMoney(stats.getMoney() + credits - penalty);
                stats.setReputation(Math.min(100, Math.max(0, stats.getReputation() + repBonus - repPenalty)));
                stats.setEnergy(Math.max(0, stats.getEnergy() - 10)); // Takes 10 energy
                
                int wordCount = score / 5; // each word = 5 score points
                String msg = "Typing Hustle Complete! " + wordCount + " words typed. Earned $" + (int)credits;
                if (repBonus > 0) msg += " and +" + repBonus + " Reputation.";
                if (mistakes > 0) {
                    msg += "\n⚠️ " + mistakes + " Typos cost you $" + (int)penalty + " and -" + repPenalty + " Reputation!";
                }
                response.put("message", msg);
            } else if ("CRISIS_MANAGEMENT".equals(type)) {
                int score = (Integer) request.getOrDefault("score", 0);
                int strikes = (Integer) request.getOrDefault("strikes", 0);
                
                // Reward: $20 per server saved, +1 Confidence per 5 servers saved
                double credits = score * 20.0;
                int confidenceBonus = score / 5;
                
                // Penalty: -$50 per crash, -5 Reputation per crash, +5 Stress per crash
                double penalty = strikes * 50.0;
                int repPenalty = strikes * 5;
                int stressPenalty = strikes * 5;
                
                stats.setMoney(stats.getMoney() + credits - penalty);
                stats.setConfidence(Math.min(100, stats.getConfidence() + confidenceBonus));
                stats.setReputation(Math.max(0, stats.getReputation() - repPenalty));
                stats.setStress(Math.min(100, stats.getStress() + stressPenalty));
                stats.setEnergy(Math.max(0, stats.getEnergy() - 15)); // Takes 15 energy
                
                String msg = "Crisis Shift Ended! Saved " + score + " servers. Earned $" + (int)credits + " and +" + confidenceBonus + " Confidence.\n";
                if (strikes > 0) {
                    msg += "⚠️ But " + strikes + " servers crashed! Lost $" + (int)penalty + ", -" + repPenalty + " Reputation, and +" + stressPenalty + " Stress.";
                }
                response.put("message", msg);
            } else if ("CORPORATE_LADDER".equals(type)) {
                int level = (Integer) request.getOrDefault("score", 0);
                boolean failed = (Boolean) request.getOrDefault("failed", false);
                
                if (failed) {
                    // Massive penalty for failing
                    stats.setStress(Math.min(100, stats.getStress() + 25));
                    stats.setReputation(Math.max(0, stats.getReputation() - 15));
                    stats.setEnergy(Math.max(0, stats.getEnergy() - 20));
                    response.put("message", "You got FIRED! Lost 15 Reputation and gained 25 Stress. No payout.");
                } else {
                    // Big payout for cashing out
                    double credits = level * 100.0;
                    int repBonus = level * 2;
                    stats.setMoney(stats.getMoney() + credits);
                    stats.setReputation(Math.min(100, stats.getReputation() + repBonus));
                    stats.setEnergy(Math.max(0, stats.getEnergy() - 15));
                    response.put("message", "Safely retired at Level " + level + ". Earned $" + (int)credits + " and +" + repBonus + " Reputation.");
                }
            } else {
                return ResponseEntity.badRequest().body("Unknown minigame type.");
            }

            // Apply rivals, achievements, endings just like a normal action
            rivalService.progressRivals(profile);
            achievementManager.checkAchievements(profile);
            checkGameEnd(profile);

            profileRepository.save(profile);
            
            response.put("profile", buildGameResponse(profile).get("profile"));
            response.put("stats", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // ─── PRIVATE HELPERS ──────────────────────────────────────────────────────

    private String applyDailyBonus(PlayerProfile profile, int streak) {
        if (profile == null || !"ACTIVE".equals(profile.getStatus())) return "No active game for bonus.";
        PlayerStats stats = profile.getStats();

        if (streak >= 7) {
            stats.setMoney(stats.getMoney() + 2000);
            stats.setMotivation(Math.min(100, stats.getMotivation() + 20));
            profileRepository.save(profile);
            return "🌟 7-Day Streak! +$2000 & +20 Motivation!";
        } else if (streak >= 3) {
            stats.setKnowledge(stats.getKnowledge() + 15);
            stats.setEnergy(Math.min(100, stats.getEnergy() + 30));
            profileRepository.save(profile);
            return "⚡ 3-Day Streak! +15 Knowledge & +30 Energy!";
        } else {
            stats.setMoney(stats.getMoney() + 500);
            profileRepository.save(profile);
            return "💰 Daily Bonus! +$500 Credits!";
        }
    }

    private void advanceTime(PlayerProfile profile) {
        profile.setCurrentWeek(profile.getCurrentWeek() + 1);
        
        // Update Market and Investments
        economyEngine.updateMarketState();
        economyEngine.processInvestments(profile);

        if (profile.getCurrentWeek() > 52) {
            profile.setCurrentWeek(1);
            profile.setAge(profile.getAge() + 1);
            recordTimelineEvent(profile, "Another year passes. You are now " + profile.getAge() + " years old.",
                "MILESTONE", profile.getAge(), 1);
        }

        PlayerStats stats = profile.getStats();
        double livingCosts = 150 + (profile.getAge() - 18) * 10;
        stats.setMoney(stats.getMoney() - livingCosts);
        stats.setHappiness(Math.max(0, stats.getHappiness() - 2));

        // Health degrades slowly with age
        if (profile.getAge() > 40) {
            stats.setHealth(Math.max(0, stats.getHealth() - 1));
        }

        // Stress builds if ignored
        if (stats.getStress() > 70) {
            stats.setHappiness(Math.max(0, stats.getHappiness() - 3));
            stats.setHealth(Math.max(0, stats.getHealth() - 2));
        }
    }

    private void checkGameEnd(PlayerProfile profile) {
        PlayerStats stats = profile.getStats();

        // Check failure conditions first
        if (stats.getHealth() <= 0) {
            setEnding(profile, "FAILED", "💀 HEALTH COLLAPSE — Your body gave out. You never took care of yourself.");
        } else if (stats.getStress() >= 100) {
            setEnding(profile, "FAILED", "🔥 TOTAL BURNOUT — The pressure destroyed you. You needed to rest.");
        } else if (stats.getMoney() < -10000) {
            setEnding(profile, "FAILED", "💸 BANKRUPTCY — Crushed by debt with no way out.");
        } else if (stats.getRelationships() <= 0) {
            setEnding(profile, "FAILED", "💔 EXTREME LONELINESS — You pushed everyone away. You died alone.");
        }

        // Check win conditions (secret endings) at age 65+
        if (profile.getAge() >= 65 && "ACTIVE".equals(profile.getStatus())) {
            if (stats.getMoney() >= 1_000_000) {
                setEnding(profile, "WON", "💰 BILLIONAIRE ENDING — You amassed a fortune beyond imagination. Legacy secured.");
            } else if (stats.getHappiness() >= 90 && stats.getRelationships() >= 80 && stats.getHealth() >= 70) {
                setEnding(profile, "WON", "🌟 BALANCED LIFE ENDING — You found perfect harmony. Truly lived.");
            } else if (stats.getKnowledge() >= 300 && stats.getReputation() >= 85) {
                setEnding(profile, "WON", "📚 INDUSTRY LEGEND ENDING — Your knowledge and reputation shaped an era.");
            } else if (stats.getRelationships() >= 90 && stats.getHappiness() >= 80) {
                setEnding(profile, "WON", "👨‍👩‍👧‍👦 FAMILY LEGACY ENDING — Your loved ones carry your legacy forward forever.");
            } else if (stats.getHealth() >= 90 && stats.getStress() <= 15) {
                setEnding(profile, "WON", "🧘 SPIRITUAL MASTER ENDING — Through peace and balance, you found true enlightenment.");
            } else {
                setEnding(profile, "WON", "✅ WORKAHOLIC ENDING — You worked hard all your life. Not perfect, but honest.");
            }
        }
    }

    private void setEnding(PlayerProfile profile, String status, String reason) {
        profile.setStatus(status);
        profile.setEndReason(reason);
        recordTimelineEvent(profile, reason, status.equals("WON") ? "ACHIEVEMENT" : "CRISIS",
            profile.getAge(), profile.getCurrentWeek());

        LeaderboardEntry entry = new LeaderboardEntry();
        entry.setPlayerName(profile.getPlayerName());
        entry.setSurvivalAge(profile.getAge());
        entry.setFinalWealth(profile.getStats().getMoney());
        entry.setFinalHappiness(profile.getStats().getHappiness());
        double bal = (profile.getStats().getMoney() / 10000.0)
            + profile.getStats().getHappiness()
            + (profile.getStats().getRelationships() * 2)
            - profile.getStats().getStress();
        entry.setBalanceScore(bal);
        leaderboardRepo.save(entry);
    }

    private void checkAndRecordMilestones(PlayerProfile profile) {
        PlayerStats stats = profile.getStats();
        // Money milestones
        if (stats.getMoney() >= 100000 && stats.getMoney() < 100300) {
            recordTimelineEvent(profile, "💰 Reached $100,000 in savings! Financial security achieved.", "MONEY", profile.getAge(), profile.getCurrentWeek(), true);
        }
        if (stats.getMoney() >= 1000000 && stats.getMoney() < 1000500) {
            recordTimelineEvent(profile, "🏆 MILLIONAIRE! Your wealth crossed $1,000,000!", "MONEY", profile.getAge(), profile.getCurrentWeek(), true);
        }
        // Knowledge milestones
        if (stats.getKnowledge() >= 100 && stats.getKnowledge() < 115) {
            recordTimelineEvent(profile, "📚 Became a domain expert with 100+ knowledge points.", "CAREER", profile.getAge(), profile.getCurrentWeek(), true);
        }
        // Relationship milestone
        if (stats.getRelationships() >= 90 && stats.getRelationships() < 95) {
            recordTimelineEvent(profile, "❤️ Your social network is thriving. Deep bonds formed.", "RELATIONSHIP", profile.getAge(), profile.getCurrentWeek(), true);
        }
        // High stress warning
        if (stats.getStress() >= 80 && stats.getStress() < 85) {
            recordTimelineEvent(profile, "⚠️ Burnout warning — stress levels dangerously high.", "CRISIS", profile.getAge(), profile.getCurrentWeek());
        }
    }

    private void recordTimelineEvent(PlayerProfile profile, String description, String type, int age, int week) {
        recordTimelineEvent(profile, description, type, age, week, false);
    }

    private void recordTimelineEvent(PlayerProfile profile, String description, String type, int age, int week, boolean claimable) {
        TimelineEvent event = new TimelineEvent();
        event.setPlayerProfile(profile);
        event.setAge(age);
        event.setWeek(week);
        event.setDescription(description);
        event.setEventType(type);
        event.setClaimable(claimable);
        timelineEventRepository.save(event);
    }

    private Map<String, Object> buildGameResponse(PlayerProfile profile) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("id", profile.getId());
        profileData.put("playerName", profile.getPlayerName());
        profileData.put("age", profile.getAge());
        profileData.put("currentWeek", profile.getCurrentWeek());
        profileData.put("status", profile.getStatus());
        profileData.put("endReason", profile.getEndReason());

        response.put("profile", profileData);
        response.put("stats", profile.getStats());

        try {
            if (profile.getPendingEventJson() != null) {
                response.put("pendingEvent", objectMapper.readTree(profile.getPendingEventJson()));
            }
        } catch (Exception ignored) {}

        return response;
    }
}
