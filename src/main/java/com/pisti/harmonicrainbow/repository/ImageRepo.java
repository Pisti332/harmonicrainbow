package com.pisti.harmonicrainbow.repository;


import com.pisti.harmonicrainbow.model.Image;
import com.pisti.harmonicrainbow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ImageRepo extends JpaRepository<Image, UUID> {
    Set<Image> getImageByUser(User user);
    Image getImageByUserAndName(User user, String name);
    Integer deleteByUserAndName(User user, String name);
    List<Image> findAll();


}
