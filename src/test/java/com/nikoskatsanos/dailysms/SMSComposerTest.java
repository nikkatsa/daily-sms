package com.nikoskatsanos.dailysms;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SMSComposerTest.SMSComposerTestConfig.class)
@TestPropertySource(locations = "classpath:/application-test.properties")
@ActiveProfiles("test")
public class SMSComposerTest {

    @Configuration
    @ComponentScan(basePackageClasses = {SMSSender.class, SMSComposer.class}, useDefaultFilters = false,
            includeFilters = {
                    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SMSSender.class, BlackholeSMSSender.class, SMSComposer.class, SMSSource.FreeTextSMSSource.class})
            })
    public static class SMSComposerTestConfig {

        @Autowired
        private ApplicationContext applicationContext;

        @Bean
        @Primary
        public List<SMSSource> smsSources() {
            return Lists.newArrayList(applicationContext.getBean(SMSSource.FreeTextSMSSource.class));
        }

        @Bean
        @Primary
        public SMSSender smsSender() {
            return Mockito.spy(new BlackholeSMSSender());
        }
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SMSComposer smsComposer;

    @Autowired
    private SMSSender smsSender;

    @Before
    public void setupSMSComposerTest() {
    }

    @Test
    public void testSchedule() throws SMSSender.SMSException {
        this.smsComposer.scheduleSMS();

        Mockito.verify(this.smsSender, Mockito.atLeast(1)).sendSMS("Some free text", "userI");
    }
}