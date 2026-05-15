package com.fashionstore.mapper;

import com.fashionstore.dto.UserDTO;
import com.fashionstore.dto.UserProfileDTO;
import com.fashionstore.dto.AddressDTO;
import com.fashionstore.dto.UserSettingsDTO;
import com.fashionstore.model.User;
import com.fashionstore.model.UserProfile;
import com.fashionstore.model.Address;
import com.fashionstore.model.UserSettings;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for User Entity ↔ DTO conversions
 * Provides safe mapping without exposing sensitive data
 */
public class UserMapper {

    /**
     * Convert User Entity to UserDTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        // dto.setActive(user.isActive());
        
        // if (user.getCreatedAt() != null) {
        //     dto.setCreatedAt(user.getCreatedAt().toLocalDateTime());
        // }
        
        // Map nested objects if present
        // if (user.getProfile() != null) {
        //     dto.setProfile(toProfileDTO(user.getProfile()));
        // }
        
        // if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
        //     List<AddressDTO> addressDTOs = user.getAddresses().stream()
        //             .map(this::toAddressDTO)
        //             .collect(Collectors.toList());
        //     dto.setAddresses(addressDTOs);
        // }
        
        // if (user.getSettings() != null) {
        //     dto.setSettings(toSettingsDTO(user.getSettings()));
        // }

        return dto;
    }

    /**
     * Convert UserDTO to User Entity
     */
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUserId(dto.getUserId());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        // user.setActive(dto.getActive());
        
        // if (dto.getCreatedAt() != null) {
        //     user.setCreatedAt(java.sql.Timestamp.valueOf(dto.getCreatedAt()));
        // }

        // Map nested objects if present
        // if (dto.getProfile() != null) {
        //     user.setProfile(toProfileEntity(dto.getProfile()));
        // }
        
        // if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
        //     List<Address> addresses = dto.getAddresses().stream()
        //             .map(this::toAddressEntity)
        //             .collect(Collectors.toList());
        //     user.setAddresses(addresses);
        // }
        
        // if (dto.getSettings() != null) {
        //     user.setSettings(toSettingsEntity(dto.getSettings()));
        // }

        return user;
    }

    /**
     * Convert UserProfile Entity to UserProfileDTO
     */
    public UserProfileDTO toProfileDTO(UserProfile profile) {
        if (profile == null) {
            return null;
        }

        UserProfileDTO dto = new UserProfileDTO();
        // dto.setProfileId(profile.getProfileId());
        dto.setUserId(profile.getUserId());
        // dto.setDateOfBirth(profile.getDateOfBirth());
        // dto.setGender(profile.getGender());
        dto.setBio(profile.getBio());
        // dto.setAvatarUrl(profile.getAvatarUrl());
        // dto.setWebsite(profile.getWebsite());
        // dto.setSocialMedia(profile.getSocialMedia());
        // dto.setPreferences(profile.getPreferences());

        return dto;
    }

    /**
     * Convert UserProfileDTO to UserProfile Entity
     */
    public UserProfile toProfileEntity(UserProfileDTO dto) {
        if (dto == null) {
            return null;
        }

        UserProfile profile = new UserProfile();
        // profile.setProfileId(dto.getProfileId());
        profile.setUserId(dto.getUserId());
        // profile.setDateOfBirth(dto.getDateOfBirth());
        // profile.setGender(dto.getGender());
        profile.setBio(dto.getBio());
        // profile.setAvatarUrl(dto.getAvatarUrl());
        // profile.setWebsite(dto.getWebsite());
        // profile.setSocialMedia(dto.getSocialMedia());
        // profile.setPreferences(dto.getPreferences());

        return profile;
    }

    /**
     * Convert Address Entity to AddressDTO
     */
    public AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setAddressId(address.getAddressId());
        dto.setUserId(address.getUserId());
        // dto.setFullName(address.getFullName());
        // dto.setAddress(address.getAddress());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        // dto.setZip(address.getZip());
        dto.setCountry(address.getCountry());
        dto.setPhone(address.getPhone());
        dto.setAddressType(address.getAddressType());
        // dto.setIsDefault(address.getIsDefault());

        return dto;
    }

    /**
     * Convert AddressDTO to Address Entity
     */
    public Address toAddressEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }

        Address address = new Address();
        address.setAddressId(dto.getAddressId());
        address.setUserId(dto.getUserId());
        // address.setFullName(dto.getFullName());
        // address.setAddress(dto.getAddress());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        // address.setZip(dto.getZip());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        address.setAddressType(dto.getAddressType());
        // address.setIsDefault(dto.getIsDefault());

        return address;
    }

    /**
     * Convert UserSettings Entity to UserSettingsDTO
     */
    public UserSettingsDTO toSettingsDTO(UserSettings settings) {
        if (settings == null) {
            return null;
        }

        UserSettingsDTO dto = new UserSettingsDTO();
        // dto.setSettingId(settings.getSettingId());
        dto.setUserId(settings.getUserId());
        dto.setEmailNotifications(settings.isEmailNotifications());
        dto.setSmsNotifications(settings.isSmsNotifications());
        // dto.setOrderUpdates(settings.isOrderUpdates());
        dto.setPromotionalEmails(settings.isPromotionalEmails());
        // dto.setNewsletter(settings.isNewsletter());
        // dto.setPrivacy(settings.getPrivacy());
        dto.setLanguage(settings.getLanguage());
        // dto.setTimezone(settings.getTimezone());
        dto.setCurrency(settings.getCurrency());

        return dto;
    }

    /**
     * Convert UserSettingsDTO to UserSettings Entity
     */
    public UserSettings toSettingsEntity(UserSettingsDTO dto) {
        if (dto == null) {
            return null;
        }

        UserSettings settings = new UserSettings();
        // settings.setSettingId(dto.getSettingId());
        settings.setUserId(dto.getUserId());
        // settings.setEmailNotifications(dto.isEmailNotifications());
        // settings.setSmsNotifications(dto.isSmsNotifications());
        // settings.setOrderUpdates(dto.isOrderUpdates());
        // settings.setPromotionalEmails(dto.isPromotionalEmails());
        // settings.setNewsletter(dto.isNewsletter());
        // settings.setPrivacy(dto.getPrivacy());
        settings.setLanguage(dto.getLanguage());
        // settings.setTimezone(dto.getTimezone());
        settings.setCurrency(dto.getCurrency());

        return settings;
    }

    /**
     * Convert list of User Entities to list of UserDTOs
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of UserDTOs to list of User Entities
     */
    public List<User> toEntityList(List<UserDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update User Entity from UserDTO (partial update)
     */
    public void updateEntityFromDTO(UserDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        // if (dto.getActive() != null) {
        //     user.setActive(dto.getActive());
        // }

        // Update nested objects
        // if (dto.getProfile() != null) {
        //     if (user.getProfile() == null) {
        //         user.setProfile(toProfileEntity(dto.getProfile()));
        //     } else {
        //         updateProfileFromDTO(dto.getProfile(), user.getProfile());
        //     }
        // }
    }

    /**
     * Update UserProfile Entity from UserProfileDTO
     */
    private void updateProfileFromDTO(UserProfileDTO dto, UserProfile profile) {
        // if (dto.getDateOfBirth() != null) {
        //     profile.setDateOfBirth(dto.getDateOfBirth());
        // }
        // if (dto.getGender() != null) {
        //     profile.setGender(dto.getGender());
        // }
        if (dto.getBio() != null) {
            profile.setBio(dto.getBio());
        }
        // if (dto.getAvatarUrl() != null) {
        //     profile.setAvatarUrl(dto.getAvatarUrl());
        // }
        // if (dto.getWebsite() != null) {
        //     profile.setWebsite(dto.getWebsite());
        // }
        // if (dto.getSocialMedia() != null) {
        //     profile.setSocialMedia(dto.getSocialMedia());
        // }
        // if (dto.getPreferences() != null) {
        //     profile.setPreferences(dto.getPreferences());
        // }
    }

    /**
     * Create safe UserDTO for public display (minimal information)
     */
    public UserDTO toPublicDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        // Only include non-sensitive information for public display
        
        return dto;
    }

    /**
     * Create UserDTO for admin display (includes more information)
     */
    public UserDTO toAdminDTO(User user) {
        UserDTO dto = toDTO(user);
        // Admin can see more details but still no sensitive data
        return dto;
    }
}
