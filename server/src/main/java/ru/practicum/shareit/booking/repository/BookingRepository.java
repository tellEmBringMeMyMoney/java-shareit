package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(Long bookerId,
                                                           LocalDateTime start,
                                                           LocalDateTime end,
                                                           Sort sort);

    List<Booking> findByBooker_IdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId,
                                                               LocalDateTime start,
                                                               LocalDateTime end,
                                                               Sort sort);

    List<Booking> findByItem_Owner_IdAndEndBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, Status status, Sort sort);

    @EntityGraph(attributePaths = "booker")
    @Query("""
            select b
            from Booking b
            where b.item in :items
              and b.status != ru.practicum.shareit.booking.model.Status.REJECTED
            order by b.start asc
            """)
    List<Booking> findApprovedByItemInOrderByStartAsc(@Param("items") List<Item> items);

    boolean existsByBooker_IdAndItem_IdAndStatusAndEndBefore(Long bookerId, Long itemId, Status status, LocalDateTime end);

}