package com.nikoskatsanos.dailysms;

import com.nikoskatsanos.dailysms.SMSSource.DayOfMonthSMSSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Suite.SuiteClasses({
    SMSSourceTest.PrefixedSMSSourceTest.class,
    SMSSourceTest.DayOfWeekSMSSourceTest.class,
    SMSSourceTest.FreeTextSourceSTest.class,
})
@RunWith(Suite.class)
public class SMSSourceTest {

    @Component
    @ComponentScan(
        basePackageClasses = {SMSSource.class},
        useDefaultFilters = false,
        includeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SMSSource.class)
        })
    public static class SMSSourceTestConfig {

        @Bean
        public RestTemplate restTemplate() {
            return Mockito.mock(RestTemplate.class);
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes = {SMSSourceTestConfig.class})
    @TestPropertySource(locations = {"classpath:/application-test.properties"})
    @ActiveProfiles("test")
    public static class PrefixedSMSSourceTest {

        @Autowired
        private ApplicationContext applicationContext;

        private SMSSource.PrefixedSMSSource prefixedSMSSource;

        @Before
        public void setupPrefixedSMSSourceTest() {
            this.prefixedSMSSource = this.applicationContext.getBean(SMSSource.PrefixedSMSSource.class);
        }

        @Test
        public void testGetText() {
            assertEquals("Good morning,", this.prefixedSMSSource.getText());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes = {SMSSourceTestConfig.class})
    @TestPropertySource(locations = {"classpath:/application-test.properties"})
    @ActiveProfiles("test")
    public static class DayOfWeekSMSSourceTest {

        @Autowired
        private ApplicationContext applicationContext;

        private SMSSource.DayOfWeekSMSSource dayOfWeekSMSSource;

        @Before
        public void setupDayOfWeekSMSSourceTest() {
            this.dayOfWeekSMSSource = this.applicationContext.getBean(SMSSource.DayOfWeekSMSSource.class);
        }

        @Test
        public void testGetText() {
            assertEquals(String.format("It's %s", LocalDate.now().getDayOfWeek().toString()), this.dayOfWeekSMSSource.getText());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes = {SMSSourceTestConfig.class})
    @TestPropertySource(locations = {"classpath:/application-test.properties"})
    @ActiveProfiles("test")
    public static class FreeTextSourceSTest {

        @Autowired
        private ApplicationContext applicationContext;

        private SMSSource.FreeTextSMSSource freeTextSMSSource;

        @Before
        public void setupFreeTextSourceSTest() {
            this.freeTextSMSSource = this.applicationContext.getBean(SMSSource.FreeTextSMSSource.class);
        }

        @Test
        public void testGetText() {
            assertEquals(String.format("Some free text", LocalDate.now().getDayOfWeek().toString()), this.freeTextSMSSource.getText());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes = {SMSSourceTestConfig.class, GeekJokeSMSSourceTest.class})
    @TestPropertySource(locations = {"classpath:/application-test.properties"})
    @ActiveProfiles("test")
    public static class GeekJokeSMSSourceTest {

        @Autowired
        private ApplicationContext applicationContext;

        private RestTemplate mockRestTemplate;
        private SMSSource.GeekJokeSMSSource geekJokeSMSSource;

        @Before
        public void setupGeekJokeSMSSourceTest() {
            this.geekJokeSMSSource = this.applicationContext.getBean(SMSSource.GeekJokeSMSSource.class);
            this.mockRestTemplate = this.applicationContext.getBean(RestTemplate.class);
        }


        @Test
        public void testGetText() {
            final ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);
            Mockito.when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            Mockito.when(responseEntity.getBody()).thenReturn("Hello World!");
            Mockito.when(this.mockRestTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(responseEntity);
            assertEquals("Hello World!", this.geekJokeSMSSource.getText());
        }

        @Test
        public void testGetText_withFailHttpCode() {
            final ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);
            Mockito.when(responseEntity.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
            Mockito.when(this.mockRestTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(responseEntity);
            assertEquals("No geek joke for today :(", this.geekJokeSMSSource.getText());
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration(classes = {SMSSourceTestConfig.class})
    @TestPropertySource(locations = {"classpath:/application-test.properties"})
    @ActiveProfiles("test")
    public static class DayOfMonthSMSSourceTest {

        @Autowired
        private ApplicationContext applicationContext;

        private DayOfMonthSMSSource dayOfMonthSMSSource;

        @Before
        public void setupDateBasedSMSSourceTest() {
            this.dayOfMonthSMSSource = this.applicationContext.getBean(DayOfMonthSMSSource.class);
        }

        @Test
        public void testGetText_dateMatches() {
            assertEquals("Hello World", new DayOfMonthSMSSource(LocalDate.now().getDayOfMonth(), "Hello World").getText());
        }

        @Test
        public void testGetText_dateDoesNotMatch() {
            assertNull(this.dayOfMonthSMSSource.getText());
        }
    }
}