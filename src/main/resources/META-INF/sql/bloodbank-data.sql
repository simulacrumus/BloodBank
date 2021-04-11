-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: localhost    Database: bloodbank
-- ------------------------------------------------------
-- Server version	8.0.23
--
-- Dumping data for table `address`
--

INSERT INTO `address` VALUES (1,'123','abcd Dr.W','ottawa','ON','CA','A1B2C3',1617265125548,1617265125548,0);

--
-- Dumping data for table `blood_bank`
--

INSERT INTO `blood_bank` VALUES (1,'Bloody Bank',_binary '',1617265125519,1617265125519,0),(2,'Cheap Bloody Bank',_binary '\0',1617265125567,1617265125567,0);

--
-- Dumping data for table `person`
--

INSERT INTO `person` VALUES (1,'Shawn','Emami',1617265125540,1617265125540,0);

--
-- Dumping data for table `phone`
--

INSERT INTO `phone` VALUES (1,'0','234','5678900',1617265125545,1617265125545,0),(2,'1','432','0098765',1617265125561,1617265125561,0);

--
-- Dumping data for table `blood_donation`
--

INSERT INTO `blood_donation` VALUES (1,2,10,'B',_binary '\0',1617265125567,1617265125567,0),(2,2,10,'A',_binary '',1617265125572,1617265125572,0);

--
-- Dumping data for table `contact`
--

INSERT INTO `contact` VALUES (1,1,'Home','test@test.com',1,1617265125540,1617265125540,0),(1,2,'Work',NULL,NULL,1617265125561,1617265125561,0);

--
-- Dumping data for table `donation_record`
--

INSERT INTO `donation_record` VALUES (1,1,2,_binary '',1617265125572,1617265125572,0),(2,1,2,_binary '\0',1617265125576,1617265125576,0),(3,1,1,_binary '',1617265125577,1617265125577,0);


--
-- Dumping data for table `role`
--

INSERT INTO `security_role` VALUES (1,'ADMIN_ROLE');
INSERT INTO `security_role` VALUES (2,'USER_ROLE');

--
-- Dumping data for table `security_user`
--

INSERT INTO `security_user` VALUES (1, 'PBKDF2WithHmacSHA256:2048:DV6fsbjR/tjYK1n2rjfr7HqVI+xTnZndaE/gd3KS+eM=:miBS+qWJuaxPyxcfr6qOy+kgf+yWgYi8FUyuz9RLBcI=', 'admin', null);
INSERT INTO `security_user` VALUES (2, 'PBKDF2WithHmacSHA256:2048:PlqfSqdwG1rcSIPFmAGK+KonpMCMPjXMzfNKF3Ov/Os=:FDU02rYPF4wOPC+l+T/a1DZr5EenefUmHtlYarNYu6U=', 'cst8288', 1);

--
-- Dumping data for table `user_has_role`
--

INSERT INTO `user_has_role` VALUES (1,1);
INSERT INTO `user_has_role` VALUES (2,2);

