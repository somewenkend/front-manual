1、初始化方法：
var table = new FixedTable("dataTable", 500, 1);

    1）、"dataTable"：原生表格id（必传参数）
    2）、"500"：表格超出此高度将滚动
    3）、1：冻结的列数（从左到右冻结，选传参数，大于0且小于表格列数的数字，若不传则不冻结列）

2、销毁方法：table.destory();
调用这个方法之后，表格会恢复初始化之前的状态，若想清除实例，请手动将实例置为null。

3、重新调整表格尺寸方法：table.adjustTableSize();
调用这个方法会使插件自动重新计算表格冻结部分的尺寸，可用在监听窗口尺寸变化事件中：
$(window).resize(function() {
    table.adjustTableSize();
});