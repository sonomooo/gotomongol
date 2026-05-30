package com.gotomongol.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = ["com.gotomongol"])
@EntityScan(basePackages = ["com.gotomongol"])
@EnableJpaRepositories(basePackages = ["com.gotomongol"])
class AdminApplication

fun main(args: Array<String>) {
    runApplication<AdminApplication>(*args)
}
