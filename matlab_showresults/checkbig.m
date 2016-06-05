close all;

reducer = [58 117 234 468 937];

%his = compare('bigballs4.csv', 'bigcenter4_his1.csv')
showcentroids('bigballs4.csv', 'bigcentroids.csv');
showcentroids('balls.csv', 'smallcentroids.csv');
showcentroids('balls.csv', 'smallcentroids1.csv');

showcentroids('balls.csv', 'smallcentroids2.csv');

showcentroids('balls.csv', 'smallcentroids3.csv');

showcentroids('balls.csv', 'smallcentroids4.csv');


sse = [];
dif4 = compare('bigballs4.csv', 'bigcenter4_my60.csv')
sse = [sse dif4];
dif4 = compare('bigballs4.csv', 'bigcenter4_my120.csv')
sse = [sse dif4];
dif4 = compare('bigballs4.csv', 'bigcenter4_my240.csv')
sse = [sse dif4];
dif4 = compare('bigballs4.csv', 'bigcenter4_my480.csv')
sse = [sse dif4];
dif4 = compare('bigballs4.csv', 'bigcenter4_my960.csv')
sse = [sse dif4];

figure;
plot(reducer, sse, '+-'); hold on
