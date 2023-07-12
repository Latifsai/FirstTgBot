package io.proj3ct.LatifFirstSpringBot.repository;

import io.proj3ct.LatifFirstSpringBot.model.Ads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdsRepository extends JpaRepository<Ads, Long> {
}
