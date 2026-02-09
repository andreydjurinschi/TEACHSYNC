package org.cedacri.spring.scheduleservice.services;

import org.cedacri.spring.scheduleservice.domain.ClassRoom;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomBaseDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomCreateDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomDetailedDto;
import org.cedacri.spring.scheduleservice.mappers.class_room.ClassRoomMapper;
import org.cedacri.spring.scheduleservice.repositories.ClassRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClassRoomService {
    private final ClassRoomRepository classRoomRepository;

    public ClassRoomService(ClassRoomRepository classRoomRepository) {
        this.classRoomRepository = classRoomRepository;
    }

    public List<ClassRoomBaseDto> getAll(){
        return classRoomRepository.findAll().stream().map(ClassRoomMapper::mapToBaseDto).toList();
    }

    public ClassRoom getById(Long id){
        return getClassRoom(id);
    }

    public ClassRoomDetailedDto getWithSchedules(Long id){
        getClassRoom(id);
        ClassRoom classRoom = classRoomRepository.findWithSchedules(id);
        return ClassRoomMapper.mapToDetailedDto(classRoom);
    }

    @Transactional
    public void create(ClassRoomCreateDto classRoomCreateDto){
        ClassRoom classRoom = ClassRoomMapper.mapToEntity(classRoomCreateDto);
        classRoomRepository.save(classRoom);
    }

    private ClassRoom getClassRoom(Long id) {
        return classRoomRepository.findById(id).orElseThrow(() -> new NoSuchElementException("this classroom  does not exist"));
    }
}
