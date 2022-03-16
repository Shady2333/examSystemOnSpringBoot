# springboot整合mybaits的项目
## 启动项目

经典的mvc视图，分为以下几个模块：
- entity实体类。定义实体类的各个属性和get，set方法
- mapper类。定义findById，findAll，deleteById，add等方法，映射到数据库中。
- service接口。定义findById，findAll，deleteById，add等方法
- serviceimpl实现接口。实现findAll等方法，直接return mapper.findAll即可。
- controller层。用来将方法映射到数据库视图中。