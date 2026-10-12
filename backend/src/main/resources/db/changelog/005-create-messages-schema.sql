CREATE TABLE messages (
    id UUID PRIMARY KEY ,
    conversation_id UUID NOT NULL REFERENCES CONVERSATIONS(id) ON DELETE CASCADE ,
    sender_id UUID NOT NULL REFERENCES USERS(id) ,
    client_message_id VARCHAR(100) NOT NULL,
    sequence_number BIGINT NOT NULL ,
    message_type VARCHAR(20) NOT NULL ,
    content VARCHAR(4000) NOT NULL ,
    created_at TIMESTAMP NOT NULL ,

    UNIQUE (sender_id,client_message_id),
    UNIQUE (conversation_id,sequence_number)
);

-- missed messages = where conversation_id = ? and sequence_number > ?
-- where conversation_id = ? order by sequence_number DESC

CREATE INDEX idx_message_conversation_sequence ON messages(conversation_id,sequence_number DESC);