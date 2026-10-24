CREATE TABLE user_blocks (
    id UUID PRIMARY KEY ,
    blocker_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE ,
    blocked_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE ,
    created_at TIMESTAMP NOT NULL ,

    UNIQUE (blocker_id,blocked_id),
    CHECK ( blocked_id <> blocker_id )
)