package com.harmonicrainbow.imageservice.repository;

import com.harmonicrainbow.imageservice.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageRepo extends JpaRepository<Image, UUID> {

}
