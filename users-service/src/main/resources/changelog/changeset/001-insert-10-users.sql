-- liquibase formatted sql
-- changeset andrei:insert-10-users
INSERT INTO users (name, surname, email, password, registered_at, role) VALUES
('Andrei', 'Djurinschi', 'alice.smith@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'ADMIN'),
('Bob', 'Johnson', 'bob.johnson@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'TEACHER'),
('Carol', 'Williams', 'carol.williams@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'MANAGER'),
('David', 'Brown', 'david.brown@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'TEACHER'),
('Eve', 'Jones', 'eve.jones@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'MANAGER'),
('Frank', 'Garcia', 'frank.garcia@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'TEACHER'),
('Grace', 'Miller', 'grace.miller@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'TEACHER'),
('Hank', 'Davis', 'hank.davis@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'TEACHER'),
('Ivy', 'Martinez', 'ivy.martinez@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'TEACHER'),
('Jack', 'Lopez', 'jack.lopez@example.com', '$2a$05$geTLybQxUMKYZ8UABOXJ.eX1st.9O3IEDrwfjVh/n1hSA4e2kEjmS', '2026-01-01', 'TEACHER');
