# 防止 GenericUtil.getOuter(Any) 反射获取外部类实例引用报错
-keepclassmembers class * {* this$*;}
# 防止 BindingUtil.createBinding(Class,LayoutInflater,ViewGroup?,Boolean) 反射创建 ViewDataBinding 报错
-keepclassmembers class * extends androidx.databinding.ViewDataBinding {* inflate(...);}