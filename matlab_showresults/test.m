close all;
% points = csvread('bout.csv');
% 
% [n, dim] = size(points);
% % for i = 1 : n
% %     points(i, 1) = randi([0 5]);
% % end
% p1 = points(points(:, 1) == 0, :);
% p2 = points(points(:, 1) == 1, :);
% 
% p3 = points(points(:, 1) == 2, :);
% p4 = points(points(:, 1) == 3, :);
% p5 = points(points(:, 1) == 4, :);
% p6 = points(points(:, 1) == 5, :);
% 
% figure;
% plot(p1(:, 2), p1(:, 3), 'm*'); hold on
% plot(p2(:, 2), p2(:, 3), 'gx'); hold on
% plot(p3(:, 2), p3(:, 3), 'b+'); hold on
% plot(p4(:, 2), p4(:, 3), 'r^'); hold on
% plot(p5(:, 2), p5(:, 3), 'cs'); hold on
% plot(p6(:, 2), p6(:, 3), 'yd'); hold on
% 
% 
% points_com = csvread('bout_random.csv');
% low = min(points_com(:, 4));
% high = max(points_com(:, 4));
% 
% p1 = points_com(points_com(:, 4) == 1, :);
% p2 = points_com(points_com(:, 4) == 42, :);
% 
% p3 = points_com(points_com(:, 4) == 22, :);
% p4 = points_com(points_com(:, 4) == 22, :);
% p5 = points_com(points_com(:, 4) == 29, :);
% p6 = points_com(points_com(:, 4) == 38, :);
% 
% figure;
% % plot(p1(:, 1), p1(:, 2), 'm*'); hold on
% % plot(p2(:, 1), p2(:, 2), 'gx'); hold on
% plot(p3(:, 1), p3(:, 2), 'b+'); hold on
% % plot(p4(:, 1), p4(:, 2), 'r^'); hold on
% % plot(p5(:, 1), p5(:, 2), 'cs'); hold on
% % plot(p6(:, 1), p6(:, 2), 'yd'); hold on
% 
% 




% dataset 0, 6 - 100 reducers


% dif1 = compare('balls.csv', 'center_my.csv')
dif4 = compare('balls.csv', 'center_his.csv')
sse = [];
% dif3 = compare('balls.csv', 'center_random.csv')
dif4 = compare('balls.csv', 'center_sse_6.csv')
sse = [sse dif4];
dif4 = compare('balls.csv', 'center_sse_11.csv')
sse = [sse dif4];
dif4 = compare('balls.csv', 'center_sse_25.csv')
sse = [sse dif4];
dif4 = compare('balls.csv', 'center_sse_50.csv')
sse = [sse dif4];
dif4 = compare('balls.csv', 'center_sse_100.csv')
% sse = [sse dif4];
% reducer = [6 11 23 46 93];
% time = [71241 65434 61486 50297 45318];
% figure;
% plot(reducer, sse);
% figure;
% plot(reducer, time);
% 
% multicen = csvread('center_multi_rect_random.csv');
% figure;
% plot(multicen(:, 2), multicen(:, 3), '*');
% rate = (dif1 - dif2) / dif1
% 
% 
% 
% curve = [9227812 34268965; 9227832 10360051; 9394621 11488803; 9394653 25989309; 9394671 15157481];
% time = [70484 195646; 71181 61105; 71729 64150; 72581 138667; 71179 84220];
% axis = 1 : 5;
% figure;
% 
% b = bar(curve);
% b(2).FaceColor = 'r';
% legend('Optimized IO','Original IO')
% 
% figure;
% b = bar(time);
% b(2).FaceColor = 'r';
% legend('Optimized execution time','Original execution time')
% 
% 
% center_1-6 6 reducer for all 
% 
% sse1 = compare('balls.csv', 'center_sse_6.csv')
% his1 = compare('balls.csv', 'center_his_0.csv')
% 
% sse2 = compare('balls.csv', 'center_1.csv')
% his2 = compare('balls.csv', 'center_his_1.csv')
% 
% sse3 = compare('balls.csv', 'center_2.csv')
% his3 = compare('balls.csv', 'center_his_2.csv')
% 
% sse4 = compare('balls.csv', 'center_3.csv')
% his4 = compare('balls.csv', 'center_his_3.csv')
% 
% sse5 = compare('balls.csv', 'center_4.csv')
% his5 = compare('balls.csv', 'center_his_4.csv')
% 
% sse_bar = [sse1 his1; sse2 his2; sse3 his3; sse4 his4; sse5 his5]
% figure;
% b = bar(sse_bar);
% b(2).FaceColor = 'r';
% legend('our method SSE','PKMeans SSE')

% plot(axis, curve(:, 1), '-+b'); hold on;
% plot(axis, curve(:, 2), '-sr');






