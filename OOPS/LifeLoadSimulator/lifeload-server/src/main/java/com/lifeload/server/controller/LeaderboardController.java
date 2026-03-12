package com.lifeload.server.controller;

import com.lifeload.server.repository.LeaderboardEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardEntryRepository leaderboardRepo;

    @GetMapping("/wealth")
    public ResponseEntity<?> getTopWealth() {
        return ResponseEntity.ok(leaderboardRepo.findTopByWealth(PageRequest.of(0, 10)));
    }

    @GetMapping("/balanced")
    public ResponseEntity<?> getTopBalanced() {
        return ResponseEntity.ok(leaderboardRepo.findTopByBalanceScore(PageRequest.of(0, 10)));
    }
}
