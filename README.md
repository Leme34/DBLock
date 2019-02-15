一、@Transactional(rollbackFor = Exception.class)不重试 的条件下：
1、读取、更新浏览次数都不加锁 时，结果<实际浏览次数

2、读取浏览次数采用悲观锁（写锁），更新浏览次数不加锁 时，结果=实际浏览次数

3、读取浏览次数不加锁，更新浏览次数采用乐观锁 时，浏览记录表的增量=商品的浏览次数的增量（部分数据丢失）
   因为乐观锁获取失败后抛出的ObjectOptimisticLockingFailureException，所以部分用户的浏览记录被回滚，造成两个表都没记录
   
二、不使用@Transactional(rollbackFor = Exception.class)，而是使用@RetryOnFailure切面对获取乐观锁失败的事务重试 的条件下：
读取浏览次数不加锁，更新浏览次数采用乐观锁 时，结果=实际浏览次数

ps：根据电脑性能的不同需要增大最大重试次数才能保证全部重试成功（不造成数据丢失）