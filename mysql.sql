
/* addresses table stores all deposit addresses user has to distribute and receive deposits*/
CREATE TABLE IF NOT EXISTS addresses(
    id int auto_increment,
    primary key(id),
	address VARCHAR(64), 
	used boolean DEFAULT(false),
 	reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 	)
 	

/* transactions store all the transactions user is concerned (deposit and withdraw)*/
CREATE TABLE IF NOT EXISTS transactions(
	id int auto_increment,
    primary key(id),
	address VARCHAR(64), 
	amount VARCHAR(64),
	transaction_id VARCHAR(128),
	is_deposit boolean,
 	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 	)
 

/* currently parsed blocks which contain transactions that are extracted and processed */ 
CREATE TABLE IF NOT EXISTS blocks (
    id int auto_increment,
    primary key(id),
    block_hash varchar(255) NOT NULL,
    prev_block_hash varchar(255) NOT NULL,
    block_height bigint NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


/* currently parsed blocks which contain transactions that are extracted and processed */ 
CREATE TABLE IF NOT EXISTS withdraws (
	id int auto_increment,
    primary key(id),    
    address VARCHAR(64), 
    amount VARCHAR(64), 
    transaction_id VARCHAR(128),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


 

