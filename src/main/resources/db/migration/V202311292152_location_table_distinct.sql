DELETE l1 FROM location l1
                   JOIN (
    SELECT MIN(location_id) as min_id, address
    FROM location
    GROUP BY address
    HAVING COUNT(*) > 1
) l2 ON l1.address = l2.address
WHERE l1.location_id > l2.min_id;


CREATE INDEX idx_latitude_longitude ON location (latitude, longitude);
CREATE INDEX idx_address ON location (address);

