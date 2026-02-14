package org.cedacri.spring.scheduleservice.services;

import org.cedacri.spring.scheduleservice.domain.ClassRoom;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomBaseDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomCreateUpdateDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomDetailedDto;
import org.cedacri.spring.scheduleservice.mappers.class_room.ClassRoomMapper;
import org.cedacri.spring.scheduleservice.repositories.ClassRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClassRoomService {
    private final ClassRoomRepository classRoomRepository;

    public ClassRoomService(ClassRoomRepository classRoomRepository) {
        this.classRoomRepository = classRoomRepository;
    }

    public List<ClassRoomBaseDto> getAll() {
        return classRoomRepository.findAll().stream().map(ClassRoomMapper::mapToBaseDto).toList();
    }

    public ClassRoom getById(Long id) {
        return getClassRoom(id);
    }

    public ClassRoomDetailedDto getWithSchedules(Long id) {
        getClassRoom(id);
        ClassRoom classRoom = classRoomRepository.findWithSchedules(id);
        return ClassRoomMapper.mapToDetailedDto(classRoom);
    }

    @Transactional
    public void create(ClassRoomCreateUpdateDto classRoomCreateUpdateDto) {
        ClassRoom classRoom = ClassRoomMapper.mapToEntity(classRoomCreateUpdateDto);
        classRoomRepository.save(classRoom);
    }

    @Transactional
    public void update(Long id, ClassRoomCreateUpdateDto classRoomCreateUpdateDto) {
        ClassRoom classRoomFromDb = getClassRoom(id);
        if (StringUtils.hasText(classRoomCreateUpdateDto.getName())) {
            classRoomFromDb.setName(classRoomCreateUpdateDto.getName());
        }
        if (classRoomCreateUpdateDto.getCapacity() != null) {
            classRoomFromDb.setCapacity(classRoomCreateUpdateDto.getCapacity());
        }
        if (StringUtils.hasText(classRoomCreateUpdateDto.getPhotoUrl())) {
            classRoomFromDb.setPhotoUrl(classRoomCreateUpdateDto.getPhotoUrl());
        }
        classRoomRepository.save(classRoomFromDb);
    }

    @Transactional
    public void deleteClassRoom(Long id) {
        ClassRoom classRoom = getClassRoom(id);
        for(var sc : classRoom.getSchedules()) {
            sc.setClassRoom(null);
        }
        classRoomRepository.deleteById(id);
    }

    private ClassRoom getClassRoom(Long id) {
        return classRoomRepository.findById(id).orElseThrow(() -> new NoSuchElementException("this classroom  does not exist"));
    }
}
