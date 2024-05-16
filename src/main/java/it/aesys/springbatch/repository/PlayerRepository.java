package it.aesys.springbatch.repository;

import it.aesys.springbatch.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player,Integer> {
}
