//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.saasovation.common.port.adapter.messaging.rabbitmq;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import junit.framework.TestCase;

import com.saasovation.common.notification.Notification;
import com.saasovation.common.notification.NotificationReader;
import com.saasovation.common.notification.NotificationSerializer;
import com.saasovation.common.port.adapter.messaging.AllPhoneNumbersCounted;
import com.saasovation.common.port.adapter.messaging.AllPhoneNumbersListed;
import com.saasovation.common.port.adapter.messaging.MatchedPhoneNumbersCounted;
import com.saasovation.common.port.adapter.messaging.PhoneNumbersMatched;

public class RabbitMQPipesFiltersTest extends TestCase {

    private ExchangeListener matchtedPhoneNumberCounter;
    private PhoneNumberExecutive phoneNumberExecutive;
    private ExchangeListener phoneNumberFinder;
    private ExchangeListener totalPhoneNumbersCounter;

    private static String[] phoneNumbers = new String[] {
        "303-555-1212   John",
        "212-555-1212   Joe",
        "718-555-1212   Zoe",
        "720-555-1212   Manny",
        "312-555-1212   Jerry",
        "303-555-9999   Sally"
    };

    public RabbitMQPipesFiltersTest() {
        super();
    }

    public void testPhoneNumbersCounter() throws Exception {
        String processId = this.phoneNumberExecutive.start(phoneNumbers);

        Thread.sleep(500L);

        PhoneNumberProcess process =
                this.phoneNumberExecutive.processOfId(processId);

        assertNotNull(process);
        assertEquals(2, process.matchedPhoneNumbers());
        assertEquals(6, process.totalPhoneNumbers());
    }

    @Override
    protected void setUp() throws Exception {

        phoneNumberExecutive = new PhoneNumberExecutive();
        phoneNumberFinder = new PhoneNumberFinder();
        matchtedPhoneNumberCounter = new MatchtedPhoneNumberCounter();
        totalPhoneNumbersCounter = new TotalPhoneNumbersCounter();

        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {

        phoneNumberExecutive.close();
        phoneNumberFinder.close();
        matchtedPhoneNumberCounter.close();
        totalPhoneNumbersCounter.close();

        super.tearDown();
    }

    private MessageProducer messageProducer() {
        Exchange exchange =
            Exchange.fanOutInstance(
                    ConnectionSettings.instance(),
                    "PhoneNumberExchange",
                    true);

        MessageProducer messageProducer = MessageProducer.instance(exchange);

        return messageProducer;
    }

    private void send(Notification aNotification) {

        MessageParameters messageParameters =
                MessageParameters.durableTextParameters(
                        aNotification.typeName(),
                        Long.toString(aNotification.notificationId()),
                        aNotification.occurredOn());

        String serializedNotification =
                NotificationSerializer.instance().serialize(aNotification);

        this.messageProducer().send(serializedNotification, messageParameters);
    }

    private class PhoneNumberProcess {

        private String id;
        private int matchedPhoneNumbers;
        private int totalPhoneNumbers;

        public PhoneNumberProcess() {
            super();

            this.id = UUID.randomUUID().toString().toUpperCase();
            this.matchedPhoneNumbers = -1;
            this.totalPhoneNumbers = -1;
        }

        public boolean isCompleted() {
            return this.matchedPhoneNumbers() >= 0 && this.totalPhoneNumbers() >= 0;
        }

        public String id() {
            return this.id;
        }

        public int matchedPhoneNumbers() {
            return this.matchedPhoneNumbers;
        }

        public void setMatchedPhoneNumbers(int aMatchedPhoneNumbersCount) {
            this.matchedPhoneNumbers = aMatchedPhoneNumbersCount;
        }

        public int totalPhoneNumbers() {
            return this.totalPhoneNumbers;
        }

        public void setTotalPhoneNumbers(int aTotalPhoneNumberCount) {
            this.totalPhoneNumbers = aTotalPhoneNumberCount;
        }
    }

    private class PhoneNumberExecutive extends ExchangeListener {

        private Map<String, PhoneNumberProcess> processes;

        public PhoneNumberExecutive() {
            super();

            this.processes = new HashMap<String, PhoneNumberProcess>();
        }

        public PhoneNumberProcess processOfId(String aProcessId) {
            return this.processes.get(aProcessId);
        }

        public String start(String[] aPhoneNumbers) {

            PhoneNumberProcess process = new PhoneNumberProcess();

            synchronized (this.processes) {
                this.processes.put(process.id(), process);
            }

            String allPhoneNumbers = "";

            for (String phoneNumber : aPhoneNumbers) {
                if (!allPhoneNumbers.isEmpty()) {
                    allPhoneNumbers = allPhoneNumbers + "\n";
                }

                allPhoneNumbers = allPhoneNumbers + phoneNumber;
            }

            Notification notification =
                    new Notification(
                            1L,
                            new AllPhoneNumbersListed(
                                    process.id(),
                                    allPhoneNumbers));

            send(notification);

            System.out.println("STARTED: " + process.id());

            return process.id();
        }

        @Override
        protected String exchangeName() {
            return "PhoneNumberExchange";
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {

            NotificationReader reader = new NotificationReader(aTextMessage);

            String processId = reader.eventStringValue("processId");

            PhoneNumberProcess process = this.processes.get(processId);

            if (reader.typeName().contains("AllPhoneNumbersCounted")) {
                process.setTotalPhoneNumbers(reader.eventIntegerValue("totalPhoneNumbers"));
                System.out.println("AllPhoneNumbersCounted...");
            } else if (reader.typeName().contains("MatchedPhoneNumbersCounted")) {
                process.setMatchedPhoneNumbers(reader.eventIntegerValue("matchedPhoneNumbers"));
                System.out.println("MatchedPhoneNumbersCounted...");
            }

            if (process.isCompleted()) {
                System.out.println(
                        "Process: "
                        + process.id()
                        + ": "
                        + process.matchedPhoneNumbers()
                        + " of "
                        + process.totalPhoneNumbers()
                        + " phone numbers found.");
            }
        }

        @Override
        protected String[] listensTo() {
            return new String[] {
                    "com.saasovation.common.port.adapter.messaging.AllPhoneNumbersCounted",
                    "com.saasovation.common.port.adapter.messaging.MatchedPhoneNumbersCounted",
            };
        }
    }

    private class PhoneNumberFinder extends ExchangeListener {

        public PhoneNumberFinder() {
            super();
        }

        @Override
        protected String exchangeName() {
            return "PhoneNumberExchange";
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {
            System.out.println("AllPhoneNumbersListed (to match)...");

            NotificationReader reader = new NotificationReader(aTextMessage);

            String allPhoneNumbers = reader.eventStringValue("allPhoneNumbers");

            String[] allPhoneNumbersToSearch = allPhoneNumbers.split("\n");

            String foundPhoneNumbers = "";

            for (String phoneNumber : allPhoneNumbersToSearch) {
                if (phoneNumber.contains("303-")) {
                    if (!foundPhoneNumbers.isEmpty()) {
                        foundPhoneNumbers = foundPhoneNumbers + "\n";
                    }
                    foundPhoneNumbers = foundPhoneNumbers + phoneNumber;
                }
            }

            Notification notification =
                    new Notification(
                            1L,
                            new PhoneNumbersMatched(
                                    reader.eventStringValue("processId"),
                                    foundPhoneNumbers));

            send(notification);
        }

        @Override
        protected String[] listensTo() {
            return new String[] { "com.saasovation.common.port.adapter.messaging.AllPhoneNumbersListed" };
        }
    }

    private class MatchtedPhoneNumberCounter extends ExchangeListener {

        public MatchtedPhoneNumberCounter() {
            super();
        }

        @Override
        protected String exchangeName() {
            return "PhoneNumberExchange";
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {

            System.out.println("PhoneNumbersMatched (to count)...");

            NotificationReader reader = new NotificationReader(aTextMessage);

            String allMatchedPhoneNumbers = reader.eventStringValue("matchedPhoneNumbers");

            String[] allPhoneNumbersToCount = allMatchedPhoneNumbers.split("\n");

            Notification notification =
                    new Notification(
                            1L,
                            new MatchedPhoneNumbersCounted(
                                    reader.eventStringValue("processId"),
                                    allPhoneNumbersToCount.length));

            send(notification);
        }

        @Override
        protected String[] listensTo() {
            return new String[] { "com.saasovation.common.port.adapter.messaging.PhoneNumbersMatched" };
        }
    }

    private class TotalPhoneNumbersCounter extends ExchangeListener {

        public TotalPhoneNumbersCounter() {
            super();
        }

        @Override
        protected String exchangeName() {
            return "PhoneNumberExchange";
        }

        @Override
        protected void filteredDispatch(String aType, String aTextMessage) {

            System.out.println("AllPhoneNumbersListed (to total)...");

            NotificationReader reader = new NotificationReader(aTextMessage);

            String allPhoneNumbers = reader.eventStringValue("allPhoneNumbers");

            String[] allPhoneNumbersToCount = allPhoneNumbers.split("\n");

            Notification notification =
                    new Notification(
                            1L,
                            new AllPhoneNumbersCounted(
                                    reader.eventStringValue("processId"),
                                    allPhoneNumbersToCount.length));

            send(notification);
        }

        @Override
        protected String[] listensTo() {
            return new String[] { "com.saasovation.common.port.adapter.messaging.AllPhoneNumbersListed" };
        }
    }
}
