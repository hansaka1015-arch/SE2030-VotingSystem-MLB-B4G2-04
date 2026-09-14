package org.example.voting.event;

import org.example.voting.model.Vote;
import org.springframework.context.ApplicationEvent;

public class VoteRecordedEvent extends ApplicationEvent {
    private Vote vote;

    public VoteRecordedEvent(Object source, Vote vote) {
        super(source);
        this.vote = vote;
    }

    public Vote getVote() {
        return vote;
    }
}