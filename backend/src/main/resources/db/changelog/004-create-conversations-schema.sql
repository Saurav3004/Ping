CREATE TABLE CONVERSATIONS (
    id UUID PRIMARY KEY ,
    type VARCHAR(20) NOT NULL ,
    title VARCHAR(120),
    created_by UUID NOT NULL REFERENCES USERS (id),
    next_message_sequence BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL ,
    updated_at TIMESTAMP NOT NULL
);