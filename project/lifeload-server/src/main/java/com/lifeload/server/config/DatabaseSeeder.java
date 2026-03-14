package com.lifeload.server.config;

import com.lifeload.server.entity.*;
import com.lifeload.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired private GameEventRepository eventRepo;
    @Autowired private TraitRepository traitRepo;
    @Autowired private SkillRepository skillRepo;
    @Autowired private AchievementRepository achievementRepo;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Clear old events to avoid duplication and ensure 150+ new ones
        eventRepo.deleteAll();
        
        seedTraits();
        seedSkills();
        seedAchievements();
        seedEvents();
    }

    private void seedTraits() {
        if (traitRepo.count() > 0) return;
        saveTrait("GENIUS", "Genius", "Learn skills 60% faster.");
        saveTrait("SOCIAL", "Social Butterfly", "Build relationships 50% faster.");
        saveTrait("AMBITIOUS", "Ambitious", "Earn 50% more from work.");
        saveTrait("CALM", "Calm", "Gain 50% less stress from all sources.");
        saveTrait("CREATIVE", "Creative", "Freelance work is 30% more effective.");
    }

    private void saveTrait(String id, String name, String desc) {
        Trait t = new Trait();
        t.setId(id);
        t.setName(name);
        t.setDescription(desc);
        traitRepo.save(t);
    }

    private void seedSkills() {
        if (skillRepo.count() > 0) return;
        saveSkill("PROGRAMMING", "Programming", "Tech", "Coding and software dev.");
        saveSkill("FINANCE", "Finance", "Economy", "Managing money and markets.");
        saveSkill("PUBLIC_SPEAKING", "Public Speaking", "Social", "Communication and leadership.");
        saveSkill("COOKING", "Cooking", "Lifestyle", "Preparing healthy meals.");
        saveSkill("INVESTING", "Investing", "Economy", "Building long-term wealth.");
    }

    private void saveSkill(String id, String name, String cat, String desc) {
        Skill s = new Skill();
        s.setId(id);
        s.setName(name);
        s.setCategory(cat);
        s.setDescription(desc);
        skillRepo.save(s);
    }

    private void seedAchievements() {
        if (achievementRepo.count() > 0) return;
        saveAchievement("FIRST_10K", "Saver", "Reach $10,000 in savings.", "stats.money >= 10000");
        saveAchievement("MILLIONAIRE", "Tycoon", "Reach $1,000,000 in wealth.", "stats.money >= 1000000");
        saveAchievement("DOMAIN_EXPERT", "Scholar", "Reach 100 Knowledge.", "stats.knowledge >= 100");
        saveAchievement("STRESS_MASTER", "Zen", "Reach age 50 with < 20 stress.", "age >= 50 && stats.stress < 20");
    }

    private void saveAchievement(String id, String title, String desc, String script) {
        Achievement a = new Achievement();
        a.setId(id);
        a.setTitle(title);
        a.setDescription(desc);
        a.setConditionScript(script);
        achievementRepo.save(a);
    }

    private void seedEvents() {
        String[] titles = {
            "The Coding Bootcamp", "Corporate Whistleblower", "Crypto Fever", "Family Inheritance",
            "Health Retreat", "Viral Social Post", "Startup Pitch", "Stock Market Tip",
            "Neighborhood Conflict", "Childhood Dream", "Late Night Burnout", "Gym Accident",
            "Mentorship Offer", "Luxury Temptation", "Old Flame Reconnects", "Identity Theft",
            "Public Speaking Gig", "Sudden Inspiration", "Real Estate Bubble", "Tax Audit",
            "Volunteer Request", "High Stakes Gamble", "Office Politics", "Academic Breakthrough",
            "Strange Hobby", "New Pet", "Quiet Weekend", "Mid-life Crisis", "Zen Awakening",
            "Lost Wallet", "Traffic Ticket", "Charity Auction", "Networking Mixer", "Unplugged Vacation",
            "Rare Collectible", "Home Repair", "Side Hustle Boom", "Industry Conference",
            "Fitness Milestone", "Bad Investment", "Family Reunion", "Back Pain", "Promotion Offer",
            "Layoff Rumors", "New Skill Opportunity", "Romantic Dinner", "Friend in Need",
            "Sudden Bonus", "Energy Drink Addiction", "Sleep Deprivation"
        };
        
        Random random = new Random();
        for (int i = 0; i < 155; i++) {
            String title = titles[random.nextInt(titles.length)] + " (" + (i + 1) + ")";
            Integer minAge = random.nextBoolean() ? 18 + random.nextInt(20) : null;
            Double maxMoney = random.nextBoolean() ? (double)(5000 + random.nextInt(100000)) : null;
            
            createEvent(title, "Random Life Event #" + (i + 1) + ". What will you do?", minAge, null, null, maxMoney,
                "Risky Choice", 0.4, 10, -10, 20, 20, 2000, 5, 0, 5, 10, 10,
                "Safe Choice", 0.0, 0, 5, 5, -5, 0, 2, 2, 0, 0, 5);
        }
    }

    private void createEvent(String title, String desc, Integer minAge, Integer maxAge, Integer minStress, Double maxMoney,
                             String opt1Label, double opt1Risk, int h1, int hp1, double m1, int k1, int e1, int r1, int rp1, int s1, int c1, int mt1,
                             String opt2Label, double opt2Risk, int h2, int hp2, double m2, int k2, int e2, int r2, int rp2, int s2, int c2, int mt2) {
        
        GameEvent event = new GameEvent();
        event.setTitle(title);
        event.setDescription(desc);
        event.setMinAge(minAge);
        event.setMaxAge(maxAge);
        event.setMinStress(minStress);
        event.setMaxMoney(maxMoney);
        event.setOptions(new ArrayList<>());

        EventOption o1 = new EventOption();
        o1.setLabel(opt1Label);
        o1.setRiskProbability(opt1Risk);
        o1.setImpact(createImpact(h1, hp1, m1, k1, e1, r1, rp1, s1, c1, mt1));
        o1.setFailImpact(createImpact(-Math.abs(h1)-10, -20, -Math.abs(m1)-500, 0, -Math.abs(e1)-10, -10, -10, 20, -10, -10));

        EventOption o2 = new EventOption();
        o2.setLabel(opt2Label);
        o2.setRiskProbability(opt2Risk);
        o2.setImpact(createImpact(h2, hp2, m2, k2, e2, r2, rp2, s2, c2, mt2));
        o2.setFailImpact(createImpact(-10, -10, -100, 0, -10, 0, 0, 10, 0, 0));

        event.getOptions().add(o1);
        event.getOptions().add(o2);
        eventRepo.save(event);
    }

    private StatImpact createImpact(int h, int hp, double m, int k, int e, int r, int rp, int s, int c, int mt) {
        StatImpact si = new StatImpact();
        si.setHealthDelta(h);
        si.setHappinessDelta(hp);
        si.setMoneyDelta(m);
        si.setKnowledgeDelta(k);
        si.setEnergyDelta(e);
        si.setRelationshipsDelta(r);
        si.setReputationDelta(rp);
        si.setStressDelta(s);
        si.setConfidenceDelta(c);
        si.setMotivationDelta(mt);
        return si;
    }
}
