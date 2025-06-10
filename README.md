# tracking application is a maven based spring boot using Java 1.8
# Application uses two different strategy
# 1. Uses concurrent hash map - for multiple instances running simultanously tracking id can be duplicated
# 2. Uses potgres database - using database will prevent duplicate tracking id