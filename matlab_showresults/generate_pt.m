close all;
mu = [2,3];
sigma = [1,1.5;1.5,3];
rng default  % For reproducibility
r = mvnrnd(mu,sigma,100);
y = randgen([4 5],[3 0;0 3],[15 5],[6 0;0 6],[5 13],[4 0;0 4])
y1 = randgen([15 15], [5 0; 0 5]);
y = y';
y = [y;y1'];
figure;
plot(y(: ,1), y(:, 2), '*');

csvwrite('bigballs4.csv', y);

% csvwrite('balls.csv', y);
