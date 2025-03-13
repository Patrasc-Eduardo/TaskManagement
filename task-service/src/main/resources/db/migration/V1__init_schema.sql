
CREATE TABLE app_users (
    id           BIGSERIAL NOT NULL,
    kc_user_id   VARCHAR(255) NOT NULL,
    username     VARCHAR(255) NOT NULL,
    email        VARCHAR(255),
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    CONSTRAINT pk_app_users PRIMARY KEY (id),
    CONSTRAINT uq_app_users_kc_user_id UNIQUE (kc_user_id)
);


CREATE INDEX idx_app_users_username ON app_users(username);


CREATE TABLE tasks (
    id          BIGSERIAL NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    due_date    TIMESTAMP,             -- or TIMESTAMP WITH TIME ZONE, depending on your needs
    status      VARCHAR(50),           -- storing enum as string
    owner_id    BIGINT,
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT fk_tasks_owner FOREIGN KEY (owner_id) REFERENCES app_users(id)
);

CREATE INDEX idx_tasks_status    ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_owner_id ON tasks(owner_id);


CREATE TABLE task_shares (
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_task_shares PRIMARY KEY (task_id, user_id),
    CONSTRAINT fk_task_shares_task FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_task_shares_user FOREIGN KEY (user_id) REFERENCES app_users(id)
);

CREATE INDEX idx_task_shares_task_id ON task_shares(task_id);
CREATE INDEX idx_task_shares_user_id ON task_shares(user_id);

