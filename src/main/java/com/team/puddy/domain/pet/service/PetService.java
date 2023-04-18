package com.team.puddy.domain.pet.service;

import com.team.puddy.domain.image.service.ImageService;
import com.team.puddy.domain.pet.domain.Pet;
import com.team.puddy.domain.pet.dto.request.RequestPetDto;
import com.team.puddy.domain.pet.dto.request.UpdatePetDto;
import com.team.puddy.domain.pet.dto.response.ResponsePetDto;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.error.ErrorCode;
import com.team.puddy.global.error.exception.NotFoundException;
import com.team.puddy.global.mapper.PetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PetService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final PetMapper petMapper;

    public void addPet(Long userId, MultipartFile file, RequestPetDto request) throws IOException {
        User findUser = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Pet pet = petMapper.toEntity(request);

        imageService.saveImagePet(pet,file);

        findUser.setPet(pet);
    }

    @Transactional(readOnly = true)
    public ResponsePetDto getPetByUserId(Long userId) {
        User findUser = userRepository.findByIdWithPet(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Pet findPet = Optional.ofNullable(findUser.getPet()).orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND));

        if (findPet.getImage() == null) {
            return petMapper.toDto(findPet,"");
        }
        return petMapper.toDto(findPet,findPet.getImage().getImagePath());
    }

    @Transactional
    public void updatePet(UpdatePetDto updateDto, Long userId, MultipartFile file) throws IOException {
        Pet findPet = userRepository.findByIdWithPet(userId).orElseThrow(() -> new NotFoundException(ErrorCode.PET_NOT_FOUND))
                .getPet();

        imageService.updateImagePet(findPet,file);

        findPet.updatePet(updateDto);
    }
}
