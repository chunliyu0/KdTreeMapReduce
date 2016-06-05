close all;
figure;

plot(k(:, 1), k(:, 2), 'o'); hold on
plot(A(:, 1), A(:, 2), 'ro');
PlotAxisAtOrigin(0, 0)
hline(0)
vline(0)
