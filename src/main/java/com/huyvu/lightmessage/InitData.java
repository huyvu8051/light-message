package com.huyvu.lightmessage;

import com.github.javafaker.Faker;
import com.huyvu.lightmessage.entity.MessageKafkaDTO;
import com.huyvu.lightmessage.jpa.model.Conversation;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.jpa.model.UserProfile;
import com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo;
import com.huyvu.lightmessage.jpa.repo.MemberJpaRepo;
import com.huyvu.lightmessage.jpa.repo.UserProfileJpaRepo;
import com.huyvu.lightmessage.r2.R2MemberRepo;
import com.huyvu.lightmessage.service.RealtimeSendingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Slf4j
@Configuration
@ConditionalOnProperty("generate-data")
public class InitData implements ApplicationRunner {
    private final UserProfileJpaRepo upRepo;
    private final ConversationJpaRepo convRepo;
    private final Faker faker;
    private final MemberJpaRepo memberJpaRepo;
    private final RealtimeSendingService realtimeSendingService;
    private final R2MemberRepo r2MemberRepo;


    public InitData(UserProfileJpaRepo upRepo, ConversationJpaRepo convRepo, MemberJpaRepo memberJpaRepo, RealtimeSendingService realtimeSendingService, R2MemberRepo r2MemberRepo) {
        this.upRepo = upRepo;
        this.convRepo = convRepo;
        this.realtimeSendingService = realtimeSendingService;
        this.r2MemberRepo = r2MemberRepo;
        faker = new Faker(Locale.of("vi"));
        this.memberJpaRepo = memberJpaRepo;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!upRepo.findAll().isEmpty()) return;

        var userProfiles = upRepo.saveAll(IntStream.range(0, 20).mapToObj(e -> {
            var entity = new UserProfile();
            var name = faker.name();
            entity.setUsername(name.username());
            entity.setName(name.fullName());
            return entity;
        }).toList());

        var conversations = convRepo.saveAll(IntStream.range(0, 20).mapToObj(e -> {
            var conv = new Conversation();
            conv.setName(faker.funnyName().name());
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


    @Scheduled(fixedRateString = "${fixed-rate}")
    public void reportCurrentTime() {
        var convId = faker.number().numberBetween(1, 20);
        var mk = MessageKafkaDTO.builder()
                .id(faker.number().randomNumber())
                .content(faker.lorem().paragraph())
                .convId(convId)
                .sentAt(OffsetDateTime.now().toString())
                .memberIds(LongStream.range(1, 20).boxed().toList())
                .build();
        realtimeSendingService.sendMessageNotification(convId, mk);
    }

    @Scheduled(fixedRate = 5000)
    public void scheduled() {
        var byUserIdAndConversationId = r2MemberRepo.findByUserIdAndConversationId(2, 3);
        byUserIdAndConversationId.subscribe(member -> System.out.println(member.toString()));
    }
}
