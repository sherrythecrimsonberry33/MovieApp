-- MovieTheater Database Setup
-- This script sets up the database schema for the AcmePlex Movie Theater System

-- Create database user and grant privileges
CREATE USER IF NOT EXISTS 'movieadmin'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON MovieTheater.* TO 'movieadmin'@'localhost';
FLUSH PRIVILEGES;

-- Create and use database
CREATE DATABASE IF NOT EXISTS MovieTheater;
USE MovieTheater;

-- Movies table
CREATE TABLE IF NOT EXISTS movies (
    id INT NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    poster_url VARCHAR(255) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    synopsis TEXT NOT NULL,
    rating DECIMAL(3,1) NOT NULL,
    age_rating VARCHAR(10) NOT NULL,
    duration INT NOT NULL,
    PRIMARY KEY (id)
);

-- Registered users table
CREATE TABLE IF NOT EXISTS registered_users (
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    membership_start_date TIMESTAMP NOT NULL,
    last_annual_fee_payment TIMESTAMP NOT NULL,
    card_number VARCHAR(19) NOT NULL,
    card_expiry_month INT NOT NULL,
    card_expiry_year INT NOT NULL,
    card_cvv VARCHAR(4) NOT NULL,
    card_holder_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (email)
);

-- Movie timings table
CREATE TABLE IF NOT EXISTS movie_timings (
    id INT NOT NULL AUTO_INCREMENT,
    movie_id INT NOT NULL,
    hall_name VARCHAR(50) NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    price DOUBLE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

-- Seat bookings table
CREATE TABLE IF NOT EXISTS seat_bookings (
    id INT NOT NULL AUTO_INCREMENT,
    movie_timing_id INT NOT NULL,
    seat_row INT NOT NULL,
    seat_column INT NOT NULL,
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    ticket_id VARCHAR(255) NOT NULL,
    status ENUM('active', 'cancelled') NOT NULL DEFAULT 'active',
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id),
    UNIQUE KEY unique_seat (movie_timing_id, seat_row, seat_column),
    FOREIGN KEY (movie_timing_id) REFERENCES movie_timings(id)
);

-- Insert sample movies
INSERT INTO movies (title, poster_url, genre, synopsis, rating, age_rating, duration) VALUES
('Conclave', 'https://sample/conclave.jpg', 'Drama', 'Cardinal Lawrence is tasked with one of the world\'s most secretive and ancient events...', 7.6, 'PG', 120),
('Wicked', 'https://sample/wicked.jpg', 'Musical', 'Misunderstood because of her green skin, a young woman named Elphaba...', 7.5, '13 +', 160),
('A Real Pain', 'https://sample//realpain.jpg', 'Comedy', 'Mismatched cousins David and Benji tour Poland...', 7.5, 'R', 90),
('Venom: The Last Dance', 'https://sample/venom.jpg', 'Action', 'Eddie Brock and Venom must make a devastating decision...', 6.2, 'PG', 109),
('Mufasa the Lion King', 'https://sample/musafalionking.jpg', 'Adventure', 'Lost and alone, orphaned cub Mufasa...', 9.1, 'PG', 118);

-- Insert sample movie timings (for next 14 days)
DELIMITER //
CREATE PROCEDURE InsertMovieTimings()
BEGIN
    DECLARE startDate DATE;
    SET startDate = CURDATE();
    
    -- Insert timings for each movie in different halls
    -- Example timing insertions (you'll need to customize based on your needs)
    INSERT INTO movie_timings (movie_id, hall_name, show_date, show_time, price) VALUES
    (1, 'IMAX_SUPREME', startDate, '10:00:00', 15.00),
    (1, 'IMAX_SUPREME', startDate, '13:00:00', 15.00),
    (1, 'IMAX_SUPREME', startDate, '16:00:00', 15.00),
    (1, 'IMAX_SUPREME', startDate, '19:00:00', 18.00),
    (1, 'IMAX_SUPREME', startDate, '22:00:00', 18.00);
    
    -- Add more timing insertions as needed
END //
DELIMITER ;

-- Execute the procedure
CALL InsertMovieTimings();
