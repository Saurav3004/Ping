CREATE TABLE CONVERSATIONS (
    id UUID PRIMARY KEY ,
    type VARCHAR(20) NOT NULL ,
    title VARCHAR(120),
    created_by UUID NOT NULL REFERENCES USERS (id),
    next_message_sequence BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL ,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE CONVERSATION_MEMBERS (
    id UUID PRIMARY KEY ,
    conversation_id UUID NOT NULL REFERENCES CONVERSATIONS (id) ON DELETE CASCADE ,
    user_id UUID NOT NULL REFERENCES USERS (id),
    role VARCHAR(20) NOT NULL ,
    last_read_sequence BIGINT NOT NULL DEFAULT 0,
    joined_at TIMESTAMP NOT NULL,

    UNIQUE (conversation_id,user_id)
);