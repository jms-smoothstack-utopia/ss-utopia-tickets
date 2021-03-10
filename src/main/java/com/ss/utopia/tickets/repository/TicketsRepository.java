package com.ss.utopia.tickets.repository;

import com.ss.utopia.tickets.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketsRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByPurchaserId(UUID purchaserId);
}
