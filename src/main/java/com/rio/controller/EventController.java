package com.rio.controller;

import com.rio.dto.CreateUpdateEventDTO;
import com.rio.dto.EventDTO;
import com.rio.dto.UserDTO;
import com.rio.entity.Event;
import com.rio.entity.User;
import com.rio.repository.EventRepository;
import com.rio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final JdbcMutableAclService aclService;

    @Transactional
    @GetMapping("/{id}")
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public EventDTO getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(this::mapToEventDTO)
                .orElseThrow(() -> new RuntimeException("Event with id: '" + id + "' not found"));
    }

    @Transactional
    @GetMapping("/participating")
    @PostFilter("hasPermission(filterObject.id(), 'com.rio.entity.Event', 'READ')")
    public List<EventDTO> getParticipatingEvents(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email).stream()
                .flatMap(user -> user.getEvents().stream())
                .map(this::mapToEventDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAuthority('CREATE_EVENT')")
    public EventDTO createEvent(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestBody CreateUpdateEventDTO createEventDto) {
        User creator = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        var event = new Event();
        event.setTitle(createEventDto.title());
        event.setDescription(createEventDto.description());
        event.setStartDateTime(createEventDto.startDateTime());
        event.setEndDateTime(createEventDto.endDateTime());
        event.setCreator(creator);
        event.setUsers(getParticipants(createEventDto));

        Event saved = eventRepository.save(event);

        var sid = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication());
        var objectIdentity = new ObjectIdentityImpl(Event.class.getName(), saved.getId());
        MutableAcl acl = aclService.createAcl(objectIdentity);
        acl.setOwner(sid);
        acl.insertAce(0, BasePermission.WRITE, sid, true);
        aclService.updateAcl(acl);


        return mapToEventDTO(saved);
    }

    @PreAuthorize("hasPermission(#id, 'com.rio.entity.Event', 'WRITE')")
    @PatchMapping("/{id}")
    @Transactional
    public EventDTO updateEvent(@PathVariable Long id, @RequestBody CreateUpdateEventDTO createEventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event with id: '" + id + "' not found"));

        event.setTitle(createEventDto.title());
        event.setDescription(createEventDto.description());
        event.setStartDateTime(createEventDto.startDateTime());
        event.setEndDateTime(createEventDto.endDateTime());
        event.setUsers(new ArrayList<>(getParticipants(createEventDto)));

        Event saved = eventRepository.save(event);

        return mapToEventDTO(saved);
    }

    @PreAuthorize("hasPermission(#id, 'com.rio.entity.Event', 'DELETE')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventRepository.deleteById(id);
    }

    private EventDTO mapToEventDTO(Event event) {
        return new EventDTO(event.getId(), event.getTitle(), event.getDescription(), event.getStartDateTime(),
                event.getEndDateTime(), mapToUserDTOs(event.getUsers()));
    }

    private List<UserDTO> mapToUserDTOs(List<User> users) {
        return users.stream()
                .map(user -> new UserDTO(user.getEmail(), user.getFirstName(), user.getLastName()))
                .toList();
    }

    private List<User> getParticipants(CreateUpdateEventDTO createUpdateEventDto) {
        if (createUpdateEventDto.userIds() != null) {
            return createUpdateEventDto.userIds().stream()
                    .map(userRepository::findById)
                    .map(Optional::orElseThrow)
                    .toList();
        }

        return Collections.emptyList();
    }
}
