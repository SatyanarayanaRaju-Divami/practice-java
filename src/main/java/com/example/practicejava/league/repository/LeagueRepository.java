package com.example.practicejava.league.repository;

import com.example.practicejava.league.League;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeagueRepository extends JpaRepository<League, UUID> {
}
