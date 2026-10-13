CREATE TABLE message_receipts(
    id UUID PRIMARY KEY ,
    message_id UUID NOT NULL REFERENCES messages(id) ON DELETE CASCADE ,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE ,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP,

    UNIQUE (message_id,user_id)
);

CREATE INDEX idx_receipts_user ON message_receipts(user_id,delivered_at,read_at);