package de.fhg.aisec.ids.idscp2.idscp_core.finite_state_machine;


import de.fhg.aisec.ids.idscp2.drivers.interfaces.DapsDriver;
import de.fhg.aisec.ids.idscp2.idscp_core.Idscp2MessageHelper;
import de.fhg.aisec.ids.idscp2.idscp_core.finite_state_machine.FSM.FSM_STATE;
import de.fhg.aisec.ids.idscp2.messages.IDSCP2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.stream.Collectors;

/**
 * The Closed State of the FSM of the IDSCP2 protocol.
 * The FSM is in the Closed state either before any transition was triggered (in this case, the
 * Closed State is the FSM Start state) or after the connection was closed (in this case, the
 * Closed State is the FSM final state without any outgoing transitions)
 * <p>
 * When the FSM go from any State into the Closed State again, the FSM is locked forever and all
 * involved actors like RatDrivers and Timers will be terminated
 *
 * @author Leon Beckmann (leon.beckmann@aisec.fraunhofer.de)
 */
class StateClosed extends State {
    private static final Logger LOG = LoggerFactory.getLogger(StateClosed.class);

    public StateClosed(FSM fsm,
                       DapsDriver dapsDriver,
                       Condition onMessageLock,
                       String[] localSupportedRatSuite,
                       String[] localExpectedRatSuite) {


        /*---------------------------------------------------
         * STATE_CLOSED - Transition Description
         * ---------------------------------------------------
         * onICM: start_handshake --> {send IDSCP_HELLO, set handshake_timeout} --> STATE_WAIT_FOR_HELLO
         * ALL_OTHER_MESSAGES ---> STATE_CLOSED
         * --------------------------------------------------- */
        this.addTransition(InternalControlMessage.START_IDSCP_HANDSHAKE.getValue(), new Transition(
                event -> {

                    LOG.debug("Get DAT Token vom DAT_DRIVER");
                    byte[] dat = dapsDriver.getToken();

                    LOG.debug("Send IDSCP_HELLO");
                    IDSCP2.IdscpMessage idscpHello = Idscp2MessageHelper.
                            createIdscpHelloMessage(dat, localSupportedRatSuite, localExpectedRatSuite);

                    if (!fsm.sendFromFSM(idscpHello)) {
                        LOG.error("Cannot send IdscpHello. Close connection");
                        runEntryCode(fsm);
                        onMessageLock.signalAll();
                        return fsm.getState(FSM_STATE.STATE_CLOSED);
                    }

                    runExitCode(onMessageLock);

                    return fsm.getState(FSM.FSM_STATE.STATE_WAIT_FOR_HELLO);
                }
        ));

        // This origins from onClose(), so just ignore it
        this.addTransition(InternalControlMessage.IDSCP_STOP.getValue(), new Transition(
                event -> {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Received STOP in STATE_CLOSED, ignored.");
                    }
                    return this;
                }
        ));

        this.setNoTransitionHandler(
                event -> {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No transition available for given event {}, stack trace for analysis:\n{}",
                                event,
                                Arrays.stream(Thread.currentThread().getStackTrace())
                                        .skip(1)
                                        .map(Object::toString)
                                        .collect(Collectors.joining("\n")));
                        LOG.debug("Stay in state STATE_CLOSED");
                    }
                    return this;
                }
        );

    }

    private void runExitCode(Condition onMessageLock) {
        //State Closed exit code
        onMessageLock.signalAll(); //enables fsm.onMessage()
    }

    @Override
    void runEntryCode(FSM fsm) {
        //State Closed entry code
        LOG.debug("Switched to state STATE_CLOSED");
        fsm.shutdownFsm();
    }

}
