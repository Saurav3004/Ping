CREATE TABLE audit_events (
    id UUID PRIMARY KEY ,
    actor_id UUID,
    action VARCHAR(80) NOT NULL ,
    resource_type VARCHAR(40) NOT NULL ,
    resource_id VARCHAR(100),
    metadata_json TEXT NOT NULL ,
    occurred_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_actor_time on audit_events(actor_id,occurred_at desc )