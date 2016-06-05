function diff = compare(pointfile, cenfile, seedfile)

points = csvread(pointfile);
cen = csvread(cenfile);
if nargin == 3
    seed = csvread(seedfile);
end
[nc, dim] = size(cen);
[np, dim] = size(points);
pwithcluster = zeros(np, dim + 1);

diff = 0;
for i = 1 : np
    p = points(i, :);
    prep = repmat(p, nc, 1);
    dist = (cen - prep).*(cen - prep);
    dist = sum(dist, 2);
    [m, index] = min(dist);
    diff = diff + m;
    pwithcluster(i, 1 : dim) = p;
    pwithcluster(i, dim + 1) = index;
end


p1 = pwithcluster(pwithcluster(:, dim + 1) == 1, :);
p2 = pwithcluster(pwithcluster(:, dim + 1) == 2, :);

p3 = pwithcluster(pwithcluster(:, dim + 1) == 3, :);
p4 = pwithcluster(pwithcluster(:, dim + 1) == 4, :);

figure;
plot(p1(:, 1), p1(:, 2), 'r*'); hold on
plot(p2(:, 1), p2(:, 2), 'go'); hold on
plot(p3(:, 1), p3(:, 2), 'bo'); hold on
if ~isempty(p4)
    plot(p4(:, 1), p4(:, 2), 'co'); hold on

end
figure;
% plot(points(:, 1), points(:, 2), 'r*'); hold on
% if nargin == 3
%     plot(seed(:, 1), seed(:, 1), 'X');
% end


