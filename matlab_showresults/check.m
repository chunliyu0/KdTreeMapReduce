close all;

reducer = [6 11 23 46 93];

sse = [];
dif4 = compare('balls.csv', 'center_hier_6.csv')
sse = [sse dif4];
dif4 = compare('balls.csv', 'center_hier_12.csv')
sse = [sse dif4];
dif4 = compare('balls.csv', 'center_hier_25.csv')
sse = [sse dif4];
dif4 = compare('balls.csv', 'center_hier_50.csv')
sse = [sse dif4];
dif4 = compare('balls.csv', 'center_hier_100.csv')

sse = [sse dif4];



sse1 = [];
dif4 = compare('balls.csv', 'center_pureran_6.csv')
sse1 = [sse1 dif4];
dif4 = compare('balls.csv', 'center_pureran_12.csv')
sse1 = [sse1 dif4];
dif4 = compare('balls.csv', 'center_pureran_25.csv')
sse1 = [sse1 dif4];
dif4 = compare('balls.csv', 'center_pureran_50.csv')
sse1 = [sse1 dif4];
dif4 = compare('balls.csv', 'center_pureran_100.csv')

sse1 = [sse1 dif4];


sse2 = [];
dif4 = compare('balls.csv', 'center_topran_6.csv')
sse2 = [sse2 dif4];
dif4 = compare('balls.csv', 'center_topran_12.csv')
sse2 = [sse2 dif4];
dif4 = compare('balls.csv', 'center_topran_25.csv')
sse2 = [sse2 dif4];
dif4 = compare('balls.csv', 'center_topran_50.csv')
sse2 = [sse2 dif4];
dif4 = compare('balls.csv', 'center_topran_100.csv')

sse2 = [sse2 dif4];


sse3 = [];
dif4 = compare('balls.csv', 'center_sse_6.csv')
sse3 = [sse3 dif4];
dif4 = compare('balls.csv', 'center_sse_11.csv')
sse3 = [sse3 dif4];
dif4 = compare('balls.csv', 'center_sse_25.csv')
sse3 = [sse3 dif4];
dif4 = compare('balls.csv', 'center_sse_50.csv')
sse3 = [sse3 dif4];
dif4 = compare('balls.csv', 'center_sse_100.csv')

sse3 = [sse3 dif4];
figure;

sse4 = ones(1, 5) * his1;

plot(reducer, sse, '+-'); hold on
plot(reducer, sse1, 'o-'); hold on
plot(reducer, sse2, 's-'); hold on
plot(reducer, sse3, '*-'); hold on
plot(reducer, sse4, '^-'); 
legend('2) + a)','3) + b)', '1) + b)', '2) + b)', 'Single machine kmeans')
xlabel('Number of reducers');
ylabel('SSE');