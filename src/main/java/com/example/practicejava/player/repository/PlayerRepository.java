package com.example.practicejava.player.repository;

import com.example.practicejava.player.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
    List<Player> findByTeam_Id(UUID teamId);
    Page<Player> findByTeam_Id(UUID teamId, Pageable pageable);
}
