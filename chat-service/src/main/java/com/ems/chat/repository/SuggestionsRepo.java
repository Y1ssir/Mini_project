package com.ems.chat.repository;

import com.ems.chat.entity.Suggestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SuggestionsRepo extends JpaRepository<Suggestions, UUID> {
}
