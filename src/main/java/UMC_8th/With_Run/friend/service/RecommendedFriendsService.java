package UMC_8th.With_Run.friend.service;

import UMC_8th.With_Run.friend.dto.FriendsResponse;
import UMC_8th.With_Run.friend.repository.AllFriendsRepository;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendedFriendsService {

    private final AllFriendsRepository allFriendsRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<FriendsResponse> recommendedFriends(Long provinceId, Long cityId, Long townId, Long userId) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile myProfile = me.getProfile();

        List<User> nearbyUsers = allFriendsRepository.findUsersByRegion(
                provinceId, cityId, townId, userId
        );

        List<String> myChars = parseJsonArray(myProfile.getCharacters());
        List<String> myStyles = parseJsonArray(myProfile.getStyle());

        List<FriendsResponse> result = new ArrayList<>();

        for (User user : nearbyUsers) {
            Profile otherProfile = user.getProfile();
            if (otherProfile == null) continue;

            List<String> otherChars = parseJsonArray(otherProfile.getCharacters());
            List<String> otherStyles = parseJsonArray(otherProfile.getStyle());

            if (hasCommonTrait(myChars, myStyles, otherChars, otherStyles)) {
                List<String> common = getCommonTraits(myChars, myStyles, otherChars, otherStyles);
                result.add(FriendsResponse.builder()
                        .userId(user.getId())
                        .userName(otherProfile.getName())
                        .profileImage(otherProfile.getProfileImage())
                        .characters(otherChars)
                        .style(otherStyles)
                        .common(common)
                        .build());
            }
        }

        return result;
    }

    private boolean hasCommonTrait(List<String> myChars, List<String> myStyles,
                                   List<String> otherChars, List<String> otherStyles) {
        return myChars.stream().anyMatch(otherChars::contains) ||
                myStyles.stream().anyMatch(otherStyles::contains);
    }

    private List<String> parseJsonArray(String json) {
        try {
            if (json == null || json.isBlank()) return Collections.emptyList();
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<String> getCommonTraits(List<String> myChars, List<String> myStyles, List<String> otherChars, List<String> otherStyles) {
        List<String> common = new ArrayList<>();

        // characters 중 1개 공통 찾기
        for (String c : myChars) {
            if (otherChars.contains(c)) {
                common.add(c);
                break;
            }
        }

        // style 중 1개 공통 찾기
        for (String s : myStyles) {
            if (otherStyles.contains(s)) {
                common.add(s);
                break;
            }
        }

        return common;
    }

}
