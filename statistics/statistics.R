library(dplyr)
library(ggplot2)

setwd("/home/joao/projects/java-source-code-quality-analyzer/statistics")
data <- read.csv("report.csv", sep = ";")
classStr = c("1) Low","2) Medium","3) High","4) Very High")
data$compClassStr = classStr[data$compClass]

names(data)

cor(data[,3:11])

ggplot(data, aes(x = compHigh)) + 
    geom_histogram() + 
    scale_x_log10(
        breaks = scales::trans_breaks("log10", function(x) 10^x),
        labels = scales::trans_format("log10", function(x) round(10^x, 0))) +
    xlab(label = expression(paste("Complexidade (", "log"["10"], ")"))) +
    ylab(label = "Numero de regras") +
    ggtitle("Número de regras x Complexidade")

ggplot(data, aes(x = compClassStr)) + 
    geom_histogram(stat = "count") + 
    xlab(label = "Classe de complexidade") +
    ylab(label = "Numero de regras") +
    ggtitle("Número de regras x Classe de complexidade")

plot(compClass ~ entropy, data = data, ylab = "Complexidade por classe", xlab = "Entropia", main = "Correlação entropia e complexidade")
plot(pmdViolTypes ~ log10(pmdViol), data = data, ylab = "Quantidade de tipos de violações", xlab = expression(paste("Número de violações ","log"["10"])), main = "Correlação número de violações e tipos de violações")
plot(pmdViolTypes ~ pmdViol, data = data, ylab = "Quantidade de tipos de violações", xlab = expression(paste("Número de violações ","log"["10"])), main = "Correlação número de violações e tipos de violações")
plot(pmdViolTypes ~ entropy, data = data, ylab = "Complexidade por classe", xlab = "Entropia", main = "Correlação entropia e complexidade")
plot(pmdViolTypes ~ entropy, data = data, ylab = "Tipos de violações", xlab = "Entropia", main = "Conhecimento vs Esforço")

hist(data$pmdViolTypes, main = "Conhecimento sobre os tipos de erro", xlab = "Qtd. tipos de erros")
hist(data$pmdViol[data$pmdViol < 150], main = "Esforço para corrigir os tipos de erro", xlab = "Qtd de erros")
plot(pmdViolTypes ~ pmdViol, data = data, ylab = "Tipos de violações", xlab = "Número de violações", main = "Conhecimento vs Esforço")
plot(pmdViolTypes ~ log(pmdViol,base=exp(1)), data = data, ylab = "Tipos de violações", xlab = expression(paste("Número de violações (", "log"["e"], ")")), main = "Conhecimento vs Esforço")


temp <- aggregate(compHigh ~ compClass, data = data, FUN = length)
temp$prop = temp$compHigh / sum(temp$compHigh)

temp <- data.frame(pmdCount = data$pmdViol, pmdType = data$pmdViolTypes, compClass = data$compClass, compHigh = data$compHigh)
cor(temp)

# ALL RULES
data2 <- read.csv("report2.csv", sep = ";", colClasses = c("integer","integer","Date","factor","factor","numeric","numeric","integer","integer","integer","integer","integer","integer","numeric"))

head(data2)
dim(data2)
names(data2)

# Acumulado - Entropia x ano/mes
entropyMY <- data2 %>% 
    mutate(monthYear = format(dateCreated, "%Y-%m")) %>%
    group_by(monthYear) %>%
    summarise(total = mean(entropy), sd = sd(entropy)) %>% 
    as.data.frame()
gg <- ggplot(entropyMY, aes(x = monthYear, y = total))
gg <- gg + geom_point()
gg <- gg + theme(axis.text.x = element_text(angle = 90, hjust = 1))
gg

# Usuário x entropia
entropyMY <- data2 %>% 
    filter(userCreated == "felipevegini") %>%
    group_by(dateCreated) %>%
    summarise(total = mean(entropy)) %>% 
    as.data.frame()
gg <- ggplot(entropyMY, aes(x = dateCreated, y = total))
gg <- gg + geom_point()
gg <- gg + theme(axis.text.x = element_text(angle = 90, hjust = 1))
gg

# Média mensal
entropyMY <- data2 %>% 
    filter(format(dateCreated, "%m") %in% c("03","04","05")) %>%
    mutate(monthYear = format(dateCreated, "%Y-%m")) %>%
    group_by(monthYear) %>%
    summarise(media = mean(entropy)) %>% 
    as.data.frame()
gg <- ggplot(entropyMY, aes(x = monthYear, y = media))
gg <- gg + geom_point()
gg <- gg + theme(axis.text.x = element_text(angle = 90, hjust = 1))
gg

# Contagem de regras 
entropyMY <- data2 %>% 
    filter(format(dateCreated, "%m") %in% c("03","04","05")) %>%
    mutate(monthYear = format(dateCreated, "%Y-%m")) %>%
    group_by(monthYear) %>%
    summarise(count = n()) %>% 
    as.data.frame()
gg <- ggplot(entropyMY, aes(x = monthYear, y = count))
gg <- gg + geom_point()
gg <- gg + theme(axis.text.x = element_text(angle = 90, hjust = 1))
gg

# Número de violações
entropyMY <- data2 %>% 
    mutate(monthYear = format(dateCreated, "%Y-%m")) %>%
    group_by(monthYear) %>%
    summarise(total = sum(pmdViolations)) %>% 
    as.data.frame()
gg <- ggplot(entropyMY, aes(x = monthYear, y = total))
gg <- gg + geom_point()
gg <- gg + theme(axis.text.x = element_text(angle = 90, hjust = 1))
gg
