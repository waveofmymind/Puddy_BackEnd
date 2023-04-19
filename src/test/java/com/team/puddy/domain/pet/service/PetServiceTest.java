package com.team.puddy.domain.pet.service;

import com.team.puddy.domain.TestEntityUtils;
import com.team.puddy.domain.image.domain.Image;
import com.team.puddy.domain.image.service.ImageService;
import com.team.puddy.domain.pet.domain.Pet;
import com.team.puddy.domain.pet.dto.request.RequestPetDto;
import com.team.puddy.domain.pet.dto.request.UpdatePetDto;
import com.team.puddy.domain.pet.dto.response.ResponsePetDto;
import com.team.puddy.domain.pet.repository.PetRepository;
import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.global.mapper.PetMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("펫 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @InjectMocks
    private PetService petService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetMapper petMapper;

    @DisplayName("펫 추가 테스트")
    @Test
    public void givenRequest_whenAddPet_thenOK() throws IOException {
        Long userId = 1L;
        User user = TestEntityUtils.user();
        RequestPetDto requestPetDto = TestEntityUtils.requestPetDto();
        Pet pet = TestEntityUtils.pet();
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some-image".getBytes());

        given(userRepository.findByUserId(userId)).willReturn(Optional.of(user));
        given(petMapper.toEntity(any(RequestPetDto.class))).willReturn(pet);

        petService.addPet(userId, file, requestPetDto);

        verify(userRepository, times(1)).findByUserId(userId);
        verify(imageService, times(1)).saveImagePet(any(Pet.class), eq(file));
        verify(petMapper, times(1)).toEntity(any(RequestPetDto.class));
    }

    @DisplayName("펫 조회 테스트")
    @Test
    public void givenUserId_whenGetPet_thenOK() {
        Long userId = 1L;
        User user = TestEntityUtils.user();
        Pet pet = TestEntityUtils.pet();
        user.setPet(pet);
        Image image = TestEntityUtils.image();
        pet.setImage(image);
        ResponsePetDto responsePetDto = TestEntityUtils.responsePetDto();
        given(userRepository.findByIdWithPet(userId)).willReturn(Optional.of(user));

        petService.getPetByUserId(userId);

        assertNotNull(responsePetDto);
        assertThat(pet.getName()).isEqualTo(responsePetDto.name());
        assertThat(pet.getImage().getImagePath()).isEqualTo(responsePetDto.imagePath());

        verify(userRepository, times(1)).findByIdWithPet(userId);
    }

    @DisplayName("펫 수정 테스트")
    @Test
    public void givenUpdate_whenUpdatePet_thenOK() throws IOException {
        Long userId = 1L;
        User user = TestEntityUtils.user();
        Pet pet = TestEntityUtils.pet();
        user.setPet(pet);
        UpdatePetDto updatePetDto = TestEntityUtils.updatePetDto();
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", "some-image".getBytes());

        given(userRepository.findByIdWithPet(userId)).willReturn(Optional.of(user));

        petService.updatePet(updatePetDto, userId, file);

        verify(userRepository, times(1)).findByIdWithPet(userId);
        verify(imageService, times(1)).updateImagePet(eq(pet), eq(file));
    }






}
