package de.fhg.aisec.ids.idscp2.idscp_core.finite_state_machine;

import de.fhg.aisec.ids.idscp2.drivers.interfaces.DapsDriver;
import de.fhg.aisec.ids.idscp2.idscp_core.Idscp2MessageHelper;
import de.fhg.aisec.ids.idscp2.idscp_core.finite_state_machine.FSM.FSM_STATE;
import de.fhg.aisec.ids.idscp2.messages.IDSCP2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Wait_For_Rat State of the FSM of the IDSCP2 protocol.
 * Waits for the RatProver and RatVerifier Result to decide whether the connection will be
 * established
 *
 * @author Leon Beckmann (leon.beckmann@aisec.fraunhofer.de)
 */
public class StateWaitForRat extends State {
    private static final Logger LOG = LoggerFactory.getLogger(StateWaitForRat.class);

    public StateWaitForRat(FSM fsm,
                           Timer handshakeTimer,
                           Timer verifierHandshakeTimer,
                           Timer proverHandshakeTimer,
                           Timer ratTimer,
                           long ratTimerDelay,
                           DapsDriver dapsDriver) {


        /*---------------------------------------------------
         * STATE_WAIT_FOR_RAT - Transition Description
         * ---------------------------------------------------
         * onICM: error ---> {ratP.stop(), ratV.stop(), timeouts.stop()} ---> IDSCP_CLOSED
         * onICM: stop ---> {ratP.stop(), ratV.stop(), timeouts.stop(), send IDSCP_CLOSE} ---> IDSCP_CLOSED
         * onICM: rat_prover_ok ---> {} ---> STATE_WAIT_FOR_RAT_VERIFIER
         * onICM: rat_verifier_ok ---> {set rat timeout} ---> STATE_WAIT_FOR_RAT_PROVER
         * onICM: rat_prover_failed ---> {ratV.stop(), timeouts.stop(), send IDSCP_CLOSE} ---> STATE_CLOSED
         * onICM: rat_verifier_failed ---> {ratP.stop(), timeouts.stop(), send IDSCP_CLOSE} ---> STATE_CLOSED
         * onICM: rat_prover_msg ---> {send IDSCP_RAT_PROVER} ---> STATE_WAIT_FOR_RAT
         * onICM: rat_verifier_msg ---> {send IDSCP_RAT_VERIFIER} ---> STATE_WAIT_FOR_RAT
         * onICM: dat_timeout ---> {send DAT_EXPIRED, ratV.cancel()} ---> STATE_WAIT_FOR_DAT_AND_RAT
         * onICM: handshake_timeout ---> {send IDSCP_CLOSE} ---> STATE_CLOSED
         * onMessage: IDSCP_RAT_VERIFIER ---> {delegate to RAT_PROVER} ---> STATE_WAIT_FOR_RAT
         * onMessage: IDSCP_RAT_PROVER ---> {delegate to RAT_VERIFIER} ---> STATE_WAIT_FOR_RAT
         * onMessage: IDSCP_DAT_EXPIRED ---> {send DAT, ratP.restart()} ---> STATE_WAIT_FOR_RAT
         * onMessage: IDSCP_CLOSE ---> {ratP.stop(), ratV.stop(), timeouts.stop()} ---> IDSCP_CLOSED
         * ALL_OTHER_MESSAGES ---> {} ---> STATE_WAIT_FOR_RAT
         * --------------------------------------------------- */
        this.addTransition(InternalControlMessage.ERROR.getValue(), new Transition(
                event -> {
                    LOG.debug("An internal control error occurred");
                    return fsm.getState(FSM.FSM_STATE.STATE_CLOSED);
                }
        ));

        this.addTransition(InternalControlMessage.IDSCP_STOP.getValue(), new Transition(
                event -> {
                    LOG.debug("Send IDSC_CLOSE");
                    fsm.sendFromFSM(Idscp2MessageHelper.createIdscpCloseMessage("User close",
                            IDSCP2.IdscpClose.CloseCause.USER_SHUTDOWN));
                    return fsm.getState(FSM.FSM_STATE.STATE_CLOSED);
                }
        ));

        this.addTransition(InternalControlMessage.RAT_PROVER_OK.getValue(), new Transition(
                event -> {
                    LOG.debug("Received RAT_PROVER OK");
                    proverHandshakeTimer.cancelTimeout();
                    return fsm.getState(FSM.FSM_STATE.STATE_WAIT_FOR_RAT_VERIFIER);
                }
        ));

        this.addTransition(InternalControlMessage.RAT_VERIFIER_OK.getValue(), new Transition(
                event -> {
                    LOG.debug("Received RAT_VERIFIER OK");
                    verifierHandshakeTimer.cancelTimeout();
                    ratTimer.resetTimeout(ratTimerDelay);
                    return fsm.getState(FSM.FSM_STATE.STATE_WAIT_FOR_RAT_PROVER);
                }
        ));

        this.addTransition(InternalControlMessage.RAT_PROVER_FAILED.getValue(), new Transition(
                event -> {
                    LOG.error("RAT_PROVER failed");
                    LOG.debug("Send IDSC_CLOSE");
                    fsm.sendFromFSM(Idscp2MessageHelper.createIdscpCloseMessage("RAT_PROVER failed",
                            IDSCP2.IdscpClose.CloseCause.RAT_PROVER_FAILED));
                    return fsm.getState(FSM.FSM_STATE.STATE_CLOSED);
                }
        ));

        this.addTransition(InternalControlMessage.RAT_VERIFIER_FAILED.getValue(), new Transition(
                event -> {
                    LOG.error("RAT_VERIFIER failed");
                    LOG.debug("Send IDSC_CLOSE");
                    fsm.sendFromFSM(Idscp2MessageHelper.createIdscpCloseMessage("RAT_VERIFIER failed",
                            IDSCP2.IdscpClose.CloseCause.RAT_VERIFIER_FAILED));
                    return fsm.getState(FSM.FSM_STATE.STATE_CLOSED);
                }
        ));

        this.addTransition(InternalControlMessage.RAT_PROVER_MSG.getValue(), new Transition(
                event -> {
                    LOG.debug("Send IDSCP_RAT_PROVER");

                    if (!fsm.sendFromFSM(event.getIdscpMessage())) {
                        LOG.error("Cannot send rat prover message");
                        return fsm.getState(FSM_STATE.STATE_CLOSED);
                    }

                    return this;
                }
        ));

        this.addTransition(InternalControlMessage.RAT_VERIFIER_MSG.getValue(), new Transition(
                event -> {
                    LOG.debug("Send IDSCP_RAT_VERIFIER");

                    if (!fsm.sendFromFSM(event.getIdscpMessage())) {
                        LOG.error("Cannot send rat verifier message");
                        return fsm.getState(FSM_STATE.STATE_CLOSED);
                    }

                    return this;
                }
        ));

        this.addTransition(InternalControlMessage.DAT_TIMER_EXPIRED.getValue(), new Transition(
                event -> {
                    LOG.debug("DAT timeout, send IDSCP_DAT_EXPIRED and cancel RAT_VERIFIER");
                    fsm.stopRatVerifierDriver();

                    if (!fsm.sendFromFSM(Idscp2MessageHelper.createIdscpDatExpiredMessage())) {
                        LOG.error("Cannot send DatExpired message");
                        return fsm.getState(FSM_STATE.STATE_CLOSED);
                    }

                    LOG.debug("Start Handshake Timer");
                    handshakeTimer.resetTimeout(5);

                    return fsm.getState(FSM.FSM_STATE.STATE_WAIT_FOR_DAT_AND_RAT);
                }
        ));

        this.addTransition(InternalControlMessage.TIMEOUT.getValue(), new Transition(
                event -> {
                    LOG.debug("Handshake timeout occurred. Send IDSCP_CLOSE");
                    fsm.sendFromFSM(Idscp2MessageHelper.createIdscpCloseMessage("Handshake timeout",
                            IDSCP2.IdscpClose.CloseCause.TIMEOUT));
                    return fsm.getState(FSM.FSM_STATE.STATE_CLOSED);
                }
        ));

        this.addTransition(IDSCP2.IdscpMessage.IDSCPRATVERIFIER_FIELD_NUMBER, new Transition(
                event -> {
                    LOG.debug("Delegate received IDSCP_RAT_VERIFIER to RAT_PROVER");
                    assert event.getIdscpMessage().hasIdscpRatVerifier();
                    fsm.getRatProverDriver().delegate(event.getIdscpMessage().getIdscpRatVerifier()
                            .getData().toByteArray());

                    return this;
                }
        ));

        this.addTransition(IDSCP2.IdscpMessage.IDSCPRATPROVER_FIELD_NUMBER, new Transition(
                event -> {
                    LOG.debug("Delegate received IDSCP_RAT_PROVER to RAT_VERIFIER");
                    assert event.getIdscpMessage().hasIdscpRatProver();
                    fsm.getRatVerifierDriver().delegate(event.getIdscpMessage().getIdscpRatProver()
                            .getData().toByteArray());

                    return this;
                }
        ));

        this.addTransition(IDSCP2.IdscpMessage.IDSCPDATEXPIRED_FIELD_NUMBER, new Transition(
                event -> {
                    LOG.debug("Received IDSCP_DAT_EXPIRED. Send new DAT from DAT_DRIVER, restart RAT_PROVER");

                    if (!fsm.sendFromFSM(Idscp2MessageHelper.createIdscpDatMessage(dapsDriver.getToken()))) {
                        LOG.error("Cannot send DAT message");
                        return fsm.getState(FSM_STATE.STATE_CLOSED);
                    }

                    if (!fsm.restartRatProverDriver()) {
                        LOG.error("Cannot run Rat prover, close idscp connection");
                        return fsm.getState(FSM_STATE.STATE_CLOSED);
                    }

                    return this;
                }
        ));

        this.addTransition(IDSCP2.IdscpMessage.IDSCPCLOSE_FIELD_NUMBER, new Transition(
                event -> {
                    LOG.debug("Received IDSCP_CLOSE");
                    return fsm.getState(FSM.FSM_STATE.STATE_CLOSED);
                }
        ));

        this.setNoTransitionHandler(
                event -> {
                    LOG.debug("No transition available for given event " + event.toString());
                    LOG.debug("Stay in state STATE_WAIT_FOR_RAT");
                    return this;
                }
        );
    }

    @Override
    void runEntryCode(FSM fsm) {
        LOG.debug("Switch to state STATE_WAIT_FOR_RAT");
    }
}
