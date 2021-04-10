-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema bloodbank
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `bloodbank` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `bloodbank` ;

-- -----------------------------------------------------
-- Table `bloodbank`.`address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`address` (
  `address_id` INT NOT NULL AUTO_INCREMENT,
  `street_number` VARCHAR(10) NOT NULL,
  `street` VARCHAR(100) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `province` VARCHAR(100) NOT NULL,
  `country` VARCHAR(100) NOT NULL,
  `zipcode` VARCHAR(100) NOT NULL,
  `created` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `updated` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `version` INT NOT NULL DEFAULT '1',
  PRIMARY KEY (`address_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bloodbank`.`blood_bank`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`blood_bank` (
  `bank_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `privately_owned` BIT(1) NOT NULL,
  `created` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `updated` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `version` INT NOT NULL DEFAULT '1',
  PRIMARY KEY (`bank_id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bloodbank`.`blood_donation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`blood_donation` (
  `donation_id` INT NOT NULL AUTO_INCREMENT,
  `bank_id` INT NOT NULL,
  `milliliters` INT NOT NULL,
  `blood_group` CHAR(2) NOT NULL,
  `rhd` BIT(1) NOT NULL,
  `created` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `updated` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `version` INT NOT NULL DEFAULT '1',
  PRIMARY KEY (`donation_id`),
  INDEX `fk_blood_donation_blood_bank1_idx` (`bank_id` ASC) VISIBLE,
  CONSTRAINT `fk_blood_donation_blood_bank1`
    FOREIGN KEY (`bank_id`)
    REFERENCES `bloodbank`.`blood_bank` (`bank_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bloodbank`.`person`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`person` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `created` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `updated` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `version` INT NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bloodbank`.`phone`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`phone` (
  `phone_id` INT NOT NULL AUTO_INCREMENT,
  `country_code` VARCHAR(10) NOT NULL DEFAULT '1',
  `area_code` VARCHAR(10) NOT NULL,
  `number` VARCHAR(10) NOT NULL,
  `created` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `updated` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `version` INT NOT NULL DEFAULT '1',
  PRIMARY KEY (`phone_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bloodbank`.`contact`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`contact` (
  `person_id` INT NOT NULL,
  `phone_id` INT NOT NULL,
  `contact_type` VARCHAR(10) NOT NULL,
  `email` VARCHAR(100) NULL DEFAULT NULL,
  `address_id` INT NULL DEFAULT NULL,
  `created` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `updated` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `version` INT NOT NULL DEFAULT '1',
  PRIMARY KEY (`person_id`, `phone_id`),
  INDEX `fk_contact_phone1_idx` (`phone_id` ASC) VISIBLE,
  INDEX `fk_contact_person1_idx` (`person_id` ASC) VISIBLE,
  INDEX `fk_contact_address1` (`address_id` ASC) VISIBLE,
  CONSTRAINT `fk_contact_address1`
    FOREIGN KEY (`address_id`)
    REFERENCES `bloodbank`.`address` (`address_id`),
  CONSTRAINT `fk_contact_person1`
    FOREIGN KEY (`person_id`)
    REFERENCES `bloodbank`.`person` (`id`),
  CONSTRAINT `fk_contact_phone1`
    FOREIGN KEY (`phone_id`)
    REFERENCES `bloodbank`.`phone` (`phone_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bloodbank`.`donation_record`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`donation_record` (
  `record_id` INT NOT NULL AUTO_INCREMENT,
  `person_id` INT NOT NULL,
  `donation_id` INT NULL DEFAULT NULL,
  `tested` BIT(1) NOT NULL,
  `created` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `updated` BIGINT NOT NULL DEFAULT (unix_timestamp(now()) * 1000),
  `version` INT NOT NULL DEFAULT '1',
  PRIMARY KEY (`record_id`),
  UNIQUE INDEX `record_id_UNIQUE` (`record_id` ASC) VISIBLE,
  UNIQUE INDEX `donation_id_UNIQUE` (`donation_id` ASC) VISIBLE,
  INDEX `fk_donation_record_person1_idx` (`person_id` ASC) VISIBLE,
  INDEX `fk_donation_record_blood_donation1_idx` (`donation_id` ASC) VISIBLE,
  CONSTRAINT `fk_donation_record_blood_donation1`
    FOREIGN KEY (`donation_id`)
    REFERENCES `bloodbank`.`blood_donation` (`donation_id`),
  CONSTRAINT `fk_donation_record_person1`
    FOREIGN KEY (`person_id`)
    REFERENCES `bloodbank`.`person` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bloodbank`.`security_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`security_user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `password_hash` VARCHAR(256) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `person_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  INDEX `fk_security_user_person1_idx` (`person_id` ASC) VISIBLE,
  CONSTRAINT `fk_security_user_person1`
    FOREIGN KEY (`person_id`)
    REFERENCES `bloodbank`.`person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bloodbank`.`security_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`security_role` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE INDEX `role_id_UNIQUE` (`role_id` ASC) VISIBLE,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bloodbank`.`user_has_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bloodbank`.`user_has_role` (
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  INDEX `fk_security_user_has_security_role_security_role1_idx` (`role_id` ASC) VISIBLE,
  INDEX `fk_security_user_has_security_role_security_user1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_security_user_has_security_role_security_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `bloodbank`.`security_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_security_user_has_security_role_security_role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `bloodbank`.`security_role` (`role_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
