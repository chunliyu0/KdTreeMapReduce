function showcentroids(datafile, cenfile)
points = csvread(datafile);
cen = csvread(cenfile);

figure;
plot(points(:, 1), points(:, 2), 'c.');hold on;
plot(cen(:, 1), cen(:, 2), 'r+', 'MarkerSize', 20);