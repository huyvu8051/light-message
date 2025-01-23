package com.huyvu.lightmessage;

import com.github.javafaker.Faker;
import com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo;
import com.huyvu.lightmessage.jpa.model.Conversation;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.jpa.model.UserProfile;
import com.huyvu.lightmessage.jpa.repo.MemberJpaRepo;
import com.huyvu.lightmessage.jpa.repo.UserProfileJpaRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Slf4j
@Configuration
public class InitData implements ApplicationRunner {
    private final UserProfileJpaRepo upRepo;
    private final ConversationJpaRepo convRepo;
    private final Faker faker;
    private final MemberJpaRepo memberJpaRepo;

    public InitData(UserProfileJpaRepo upRepo, ConversationJpaRepo convRepo, MemberJpaRepo memberJpaRepo) {
        this.upRepo = upRepo;
        this.convRepo = convRepo;
        faker = new Faker(Locale.of("vi"));
        this.memberJpaRepo = memberJpaRepo;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!upRepo.findAll().isEmpty()) return;

        var userProfiles = upRepo.saveAll(IntStream.range(0, 20).mapToObj(e -> {
            var entity = new UserProfile();
            entity.setUsername(String.format("%03d", e));
            entity.setName(faker.name().fullName());
            return entity;
        }).toList());

        var conversations = convRepo.saveAll(IntStream.range(0, 20).mapToObj(e -> {
            var conv = new Conversation();
            conv.setName(String.format("%03d", e) + "_" + faker.funnyName().name());
            return conv;
        }).toList());

        var members = memberJpaRepo.saveAll(conversations.stream().map(conv -> {
            var mems = userProfiles.stream().map(userProfile -> {
                var member = new Member();
                member.setConversation(conv);
                member.setUser(userProfile);
                return member;
            }).toList();

            return mems;
        }).flatMap(List::stream).toList());


    }
}
